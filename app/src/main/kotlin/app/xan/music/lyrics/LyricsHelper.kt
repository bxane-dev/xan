/**
 * xan Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package app.xan.music.lyrics

import android.content.Context
import android.util.LruCache
import app.xan.music.constants.LyricsProviderOrderKey
import app.xan.music.constants.PreferredLyricsProvider
import app.xan.music.constants.PreferredLyricsProviderKey
import app.xan.music.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import app.xan.music.extensions.toEnum
import app.xan.music.models.MediaMetadata
import app.xan.music.utils.NetworkConnectivityObserver
import app.xan.music.utils.dataStore
import app.xan.music.utils.reportException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class LyricsHelper
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val networkConnectivity: NetworkConnectivityObserver,
) {
    /**
     * Resolves the ordered list of lyrics providers from the user's saved priority order.
     * Falls back to migrating the legacy [PreferredLyricsProvider] enum if the new order
     * preference has not been written yet, ensuring a smooth upgrade for existing users.
     */
    private suspend fun resolveLyricsProviders(): List<LyricsProvider> {
        val preferences = context.dataStore.data.first()
        val orderString = preferences[LyricsProviderOrderKey].orEmpty()

        if (orderString.isNotBlank()) {
            return LyricsProviderRegistry.getOrderedProviders(orderString)
        }

        // Migration path: place the old preferred provider first in the default order
        val preferredEnum = preferences[PreferredLyricsProviderKey]
            .toEnum(PreferredLyricsProvider.YOULYPLUS)
        val preferredName = LyricsProviderRegistry.getProviderNameForEnum(preferredEnum)
        val defaultOrder = LyricsProviderRegistry.getDefaultProviderOrder()
        val migratedOrder = listOf(preferredName) + defaultOrder.filter { it != preferredName }
        return migratedOrder.mapNotNull { LyricsProviderRegistry.getProviderByName(it) }
    }



    private val cache = LruCache<String, List<LyricsResult>>(MAX_CACHE_SIZE)
    private var currentLyricsJob: Job? = null

    suspend fun getLyrics(mediaMetadata: MediaMetadata): LyricsWithProvider {
        currentLyricsJob?.cancel()

        val cached = cache.get(mediaMetadata.id)?.firstOrNull()
        if (cached != null) {
            return LyricsWithProvider(cached.lyrics, cached.providerName)
        }

        // Check network connectivity before making network requests
        // Use synchronous check as fallback if flow doesn't emit
        val isNetworkAvailable = try {
            networkConnectivity.isCurrentlyConnected()
        } catch (e: Exception) {
            // If network check fails, try to proceed anyway
            true
        }
        
        if (!isNetworkAvailable) {
            // Still proceed but return not found to avoid hanging
            return LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")
        }

        val providers = resolveLyricsProviders()
        val scope = CoroutineScope(SupervisorJob())
        val deferred = scope.async {
            var bestFallback: LyricsWithProvider? = null
            var bestFallbackQuality = 0

            // Do not stop at the first merely-usable result. A fast plain/line-synced
            // provider used to mask a slightly slower word-synced provider later in
            // the chain. Word/syllable timing wins immediately; line sync and plain
            // text are retained only as fallbacks.
            for (provider in providers) {
                if (!provider.isEnabled(context)) continue
                try {
                    provider.getLyrics(
                        mediaMetadata.id,
                        mediaMetadata.title,
                        mediaMetadata.artists.joinToString { it.name },
                        mediaMetadata.duration,
                        mediaMetadata.album?.title,
                    ).onSuccess { lyrics ->
                        val quality = lyricSyncQuality(lyrics)
                        if (quality >= 3) {
                            return@async LyricsWithProvider(lyrics, provider.name)
                        }
                        if (quality > bestFallbackQuality) {
                            bestFallbackQuality = quality
                            bestFallback = LyricsWithProvider(lyrics, provider.name)
                        }
                    }.onFailure {
                        reportException(it)
                    }
                } catch (e: Exception) {
                    reportException(e)
                }
            }

            bestFallback ?: LyricsWithProvider(LYRICS_NOT_FOUND, "Unknown")
        }

        val result = deferred.await()
        scope.cancel()
        return result
    }

    suspend fun getAllLyrics(
        mediaId: String,
        songTitle: String,
        songArtists: String,
        duration: Int,
        album: String? = null,
        callback: (LyricsResult) -> Unit,
    ) {
        currentLyricsJob?.cancel()

        val cacheKey = "$songArtists-$songTitle".replace(" ", "")
        cache.get(cacheKey)?.let { results ->
            results.forEach {
                callback(it)
            }
            return
        }

        // Check network connectivity before making network requests
        // Use synchronous check as fallback if flow doesn't emit
        val isNetworkAvailable = try {
            networkConnectivity.isCurrentlyConnected()
        } catch (e: Exception) {
            // If network check fails, try to proceed anyway
            true
        }
        
        if (!isNetworkAvailable) {
            // Still try to proceed in case of false negative
            return
        }

        val allResult = mutableListOf<LyricsResult>()
        val providers = resolveLyricsProviders()
        currentLyricsJob = CoroutineScope(SupervisorJob()).launch {
            providers.forEach { provider ->
                if (provider.isEnabled(context)) {
                    try {
                        provider.getAllLyrics(mediaId, songTitle, songArtists, duration, album) { lyrics ->
                            val result = LyricsResult(provider.name, lyrics)
                            allResult += result
                            callback(result)
                        }
                    } catch (e: Exception) {
                        // Catch network-related exceptions like UnresolvedAddressException
                        reportException(e)
                    }
                }
            }
            cache.put(cacheKey, allResult)
        }

        currentLyricsJob?.join()
    }

    fun cancelCurrentLyricsJob() {
        currentLyricsJob?.cancel()
        currentLyricsJob = null
    }

    /**
     * 3 = word/syllable timed, 2 = line timed, 1 = plain text.
     * Supports both enhanced-LRC word timestamps and the richer timing syntax
     * returned by some YouLyPlus/LyricsPlus mirrors.
     */
    private fun lyricSyncQuality(lyrics: String): Int {
        if (lyrics.isBlank() || lyrics == LYRICS_NOT_FOUND) return 0
        val hasWordTiming =
            Regex("<\\d{1,2}:\\d{2}(?:\\.\\d{2,3})?>").containsMatchIn(lyrics) ||
                Regex("(?m)^<[^>]+:\\d+(?:\\.\\d+)?:\\d+(?:\\.\\d+)?(?:\\|[^>]+)*>$")
                    .containsMatchIn(lyrics)
        if (hasWordTiming) return 3

        val hasLineTiming =
            Regex("\\[\\d{1,2}:\\d{2}(?:\\.\\d{2,3})?]").containsMatchIn(lyrics)
        return if (hasLineTiming) 2 else 1
    }

    companion object {
        private const val MAX_CACHE_SIZE = 3
    }
}

data class LyricsResult(
    val providerName: String,
    val lyrics: String,
)

data class LyricsWithProvider(
    val lyrics: String,
    val provider: String,
)