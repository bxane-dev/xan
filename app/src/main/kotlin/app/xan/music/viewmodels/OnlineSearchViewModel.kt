/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.music.innertube.models.filterYoutubeShorts
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.SongItem
import com.music.innertube.pages.SearchSummaryPage
import com.music.innertube.pages.SearchSummary
import app.xan.music.R
import app.xan.music.constants.HideExplicitKey
import app.xan.music.constants.HideVideoSongsKey
import app.xan.music.constants.DataSaverEnabledKey
import app.xan.music.constants.HideYoutubeShortsKey
import app.xan.music.constants.YouTubeSearchFallbackKey
import app.xan.music.models.ItemsPage
import app.xan.music.utils.dataStore
import app.xan.music.utils.get
import app.xan.music.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class OnlineSearchViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val initialQuery = try {
        URLDecoder.decode(savedStateHandle.get<String>("query").orEmpty(), "UTF-8")
    } catch (e: IllegalArgumentException) {
        savedStateHandle.get<String>("query").orEmpty()
    }
    private val query = MutableStateFlow(initialQuery)
    val filter = MutableStateFlow<YouTube.SearchFilter?>(null)
    var summaryPage by mutableStateOf<SearchSummaryPage?>(null)
        private set
    var summaryLoading by mutableStateOf(true)
        private set
    var summaryError by mutableStateOf(false)
        private set
    val viewStateMap = mutableStateMapOf<String, ItemsPage?>()
    val loadingFilters = mutableStateMapOf<String, Boolean>()
    val errorFilters = mutableStateMapOf<String, Boolean>()
    val youtubeFallbackFilters = mutableStateMapOf<String, Boolean>()

    init {
        viewModelScope.launch {
            // Only fetches what isn't cached yet â€” switching filters back and forth
            // must not re-hit the network. refresh() drops the cache entry first.
            combine(query.debounce(300), filter) { searchQuery, selectedFilter ->
                searchQuery to selectedFilter
            }.collectLatest { (searchQuery, selectedFilter) ->
                if (searchQuery.isNotBlank()) load(searchQuery, selectedFilter)
            }
        }
    }

    fun updateQuery(newQuery: String) {
        if (query.value == newQuery) return
        summaryPage = null
        summaryLoading = true
        summaryError = false
        viewStateMap.clear()
        loadingFilters.clear()
        errorFilters.clear()
        youtubeFallbackFilters.clear()
        query.value = newQuery
    }

    private suspend fun load(query: String, filter: YouTube.SearchFilter?) {
        if (filter == null) {
            if (summaryPage != null) return
            summaryLoading = true
            summaryError = false
            try {
                val normalizedQuery = query.trim()
                val summaryResult = YouTube.searchSummary(normalizedQuery)
                summaryResult.exceptionOrNull()?.let {
                    if (it is CancellationException) throw it
                }
                var page = summaryResult.getOrNull()?.filteredForSearch()
                var allRequestsFailed = summaryResult.isFailure

                // A top-results response can omit its artist shelf for exact
                // person queries. Ask the dedicated artist filter when the
                // summary parser did not return any artists.
                if (page?.summaries?.any { summary -> summary.items.any { it is ArtistItem } } != true) {
                    val artistResult = YouTube.search(normalizedQuery, YouTube.SearchFilter.FILTER_ARTIST)
                    artistResult.exceptionOrNull()?.let {
                        if (it is CancellationException) throw it
                    }
                    allRequestsFailed = allRequestsFailed && artistResult.isFailure
                    val artistItems = artistResult.getOrNull()?.items
                        ?.filterIsInstance<ArtistItem>()
                        ?.distinctBy { it.id }
                        .orEmpty()
                    if (artistItems.isNotEmpty()) {
                        val existingSummaries = page?.summaries.orEmpty()
                        page = SearchSummaryPage(
                            existingSummaries + SearchSummary("Artists", artistItems),
                        )
                    }
                }

                // If the mixed-results endpoint failed completely, still try
                // the song shelf so a transient summary/parser issue does not
                // leave the search page blank.
                if (summaryResult.isFailure && page?.summaries.isNullOrEmpty()) {
                    val songResult = YouTube.search(normalizedQuery, YouTube.SearchFilter.FILTER_SONG)
                    songResult.exceptionOrNull()?.let {
                        if (it is CancellationException) throw it
                    }
                    allRequestsFailed = allRequestsFailed && songResult.isFailure
                    val songItems = songResult.getOrNull()?.items
                        ?.filterIsInstance<SongItem>()
                        ?.filterExplicit(context.dataStore.get(HideExplicitKey, false))
                        ?.filterVideoSongs(
                            context.dataStore.get(HideVideoSongsKey, false) ||
                                context.dataStore.get(DataSaverEnabledKey, false),
                        )
                        ?.filterYoutubeShorts(context.dataStore.get(HideYoutubeShortsKey, false))
                        .orEmpty()
                    if (songItems.isNotEmpty()) {
                        page = SearchSummaryPage(listOf(SearchSummary("Songs", songItems)))
                    }
                }

                val hasSongs = page?.summaries?.any { summary ->
                    summary.items.any { it is SongItem && !it.isVideoSong }
                } == true
                val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) ||
                    context.dataStore.get(DataSaverEnabledKey, false)
                if (!hasSongs && !hideVideoSongs && context.dataStore.get(YouTubeSearchFallbackKey, true)) {
                    val fallbackResult = YouTube.searchYouTubeVideos(normalizedQuery)
                    fallbackResult.exceptionOrNull()?.let {
                        if (it is CancellationException) throw it
                    }
                    allRequestsFailed = allRequestsFailed && fallbackResult.isFailure
                    val fallbackItems = fallbackResult.getOrNull()?.items
                        ?.filterIsInstance<SongItem>()
                        ?.filterExplicit(context.dataStore.get(HideExplicitKey, false))
                        .orEmpty()
                    if (fallbackItems.isNotEmpty()) {
                        page = SearchSummaryPage(
                            page?.summaries.orEmpty() + SearchSummary(
                                context.getString(R.string.youtube_fallback_results),
                                fallbackItems,
                            ),
                        )
                    }
                }

                if (!allRequestsFailed) summaryPage = page ?: SearchSummaryPage(emptyList())
                summaryError = allRequestsFailed && page?.summaries.isNullOrEmpty()
                if (summaryError) {
                    summaryResult.exceptionOrNull()?.let(::reportException)
                }
            } catch (error: CancellationException) {
                throw error
            } finally {
                summaryLoading = false
            }
        } else {
            val filterKey = filter.value
            if (viewStateMap[filterKey] != null) return
            loadingFilters[filterKey] = true
            errorFilters[filterKey] = false
            youtubeFallbackFilters[filterKey] = false
            try {
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) ||
                    context.dataStore.get(DataSaverEnabledKey, false)
                val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
                val primaryResult = YouTube.search(query.trim(), filter)
                primaryResult.exceptionOrNull()?.let {
                    if (it is CancellationException) throw it
                }
                primaryResult.onSuccess { result ->
                    val results = result.items.toMutableList()
                    var continuation = result.continuation
                    val seenContinuations = mutableSetOf<String>()
                    fun visibleResults() = results
                        .distinctBy { it.id }
                        .filterExplicit(hideExplicit)
                        .filterVideoSongs(hideVideoSongs)
                        .filterYoutubeShorts(hideYoutubeShorts)
                    while (visibleResults().size < 10 && continuation != null && seenContinuations.add(continuation)) {
                        val nextPageResult = YouTube.searchContinuation(continuation)
                        nextPageResult.exceptionOrNull()?.let {
                            if (it is CancellationException) throw it
                        }
                        val nextPage = nextPageResult.getOrNull() ?: break
                        results += nextPage.items
                        continuation = nextPage.continuation
                    }
                    viewStateMap[filterKey] =
                        ItemsPage(
                            visibleResults(),
                            continuation,
                        )
                }

                if (filter == YouTube.SearchFilter.FILTER_SONG &&
                    viewStateMap[filterKey]?.items.isNullOrEmpty() &&
                    !hideVideoSongs &&
                    context.dataStore.get(YouTubeSearchFallbackKey, true)
                ) {
                    val fallbackResult = YouTube.searchYouTubeVideos(query.trim())
                    fallbackResult.exceptionOrNull()?.let {
                        if (it is CancellationException) throw it
                    }
                    fallbackResult.onSuccess { result ->
                        viewStateMap[filterKey] = ItemsPage(
                            result.items.filterExplicit(hideExplicit).distinctBy { it.id },
                            null,
                        )
                        youtubeFallbackFilters[filterKey] = viewStateMap[filterKey]?.items?.isNotEmpty() == true
                    }.onFailure(::reportException)
                }

                if (viewStateMap[filterKey] == null) {
                    errorFilters[filterKey] = true
                    primaryResult.exceptionOrNull()?.let(::reportException)
                }
            } finally {
                loadingFilters[filterKey] = false
            }
        }
    }

    private fun SearchSummaryPage.filteredForSearch(): SearchSummaryPage {
        val hideExplicit = context.dataStore.get(HideExplicitKey, false)
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) ||
            context.dataStore.get(DataSaverEnabledKey, false)
        val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
        return filterExplicit(hideExplicit)
            .filterVideoSongs(hideVideoSongs)
            .filterYoutubeShorts(hideYoutubeShorts)
    }

    /**
     * Re-runs the current query. Bound to the pull-to-refresh gesture.
     *
     * Clears this filter's cached page first, otherwise [load] short-circuits on
     * it â€” and calls [load] directly rather than re-setting [filter], since a
     * StateFlow drops a write of the value it already holds.
     */
    fun refresh() {
        val current = filter.value
        if (current == null) {
            summaryPage = null
            summaryError = false
            summaryLoading = true
        } else {
            viewStateMap.remove(current.value)
            errorFilters[current.value] = false
            loadingFilters[current.value] = true
        }
        viewModelScope.launch { load(query.value, current) }
    }

    fun loadMore() {
        val filter = filter.value?.value
        val requestedQuery = query.value
        viewModelScope.launch {
            if (filter == null) return@launch
            val viewState = viewStateMap[filter] ?: return@launch
            val continuation = viewState.continuation
            if (continuation != null) {
                val searchResult =
                    YouTube.searchContinuation(continuation).getOrNull() ?: return@launch
                if (query.value != requestedQuery) return@launch
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
                val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
                val newItems = searchResult.items
                    .filterExplicit(hideExplicit)
                    .filterVideoSongs(hideVideoSongs)
                    .filterYoutubeShorts(hideYoutubeShorts)
                viewStateMap[filter] = ItemsPage(
                    (viewState.items + newItems).distinctBy { it.id },
                    searchResult.continuation
                )
            }
        }
    }
}
