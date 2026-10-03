package app.xan.music.playback

import com.music.innertube.models.*
import com.music.innertube.pages.*

/** Shared artist catalogue collection, separate from download state/queueing. */
suspend fun resolveArtistDownloadSongs(
    page: ArtistPage,
    fetchItems: suspend (BrowseEndpoint) -> ArtistItemsPage,
    fetchContinuation: suspend (String) -> ArtistItemsContinuationPage,
    fetchAlbum: suspend (String) -> AlbumPage,
    onSongsDiscovered: suspend (List<SongItem>) -> Unit = {},
): List<SongItem> {
    val songs = mutableListOf<SongItem>()
    val albums = linkedMapOf<String, AlbumItem>()
    val endpoints = mutableSetOf<BrowseEndpoint>()
    val discoveredIds = mutableSetOf<String>()
    suspend fun discover(items: List<YTItem>) {
        val batch = items.filterIsInstance<SongItem>().filter { discoveredIds.add(it.id) }
        songs += batch
        if (batch.isNotEmpty()) onSongsDiscovered(batch)
        items.filterIsInstance<AlbumItem>().forEach { albums[it.browseId] = it }
    }
    for (section in page.sections) {
        if (section.items.none { it is SongItem || it is AlbumItem }) continue
        discover(section.items)
        val endpoint = section.moreEndpoint
        if (endpoint != null && endpoints.add(endpoint)) {
            val first = fetchItems(endpoint)
            discover(first.items)
            var continuation = first.continuation
            val seen = mutableSetOf<String>()
            while (continuation != null) {
                check(seen.add(continuation)) { "Artist catalogue repeated a continuation token" }
                val next = fetchContinuation(continuation)
                discover(next.items)
                continuation = next.continuation
            }
        }
    }
    for (album in albums.values.toList()) discover(fetchAlbum(album.browseId).songs)
    return songs
}
