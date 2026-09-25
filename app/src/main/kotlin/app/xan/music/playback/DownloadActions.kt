/**
 * xan Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package app.xan.music.playback

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import app.xan.music.db.entities.Song
import app.xan.music.models.MediaMetadata
import app.xan.music.models.toMediaMetadata
import org.json.JSONArray
import org.json.JSONObject

/**
 * Metadata carried with an offline download.
 *
 * Media3's cache only knows the media id and opaque request.data bytes. Keeping the
 * presentation metadata here makes downloaded tracks self-describing even when the
 * network is unavailable by the time the download finishes.
 */
data class DownloadTarget(
    val id: String,
    val title: String,
    val artists: List<String> = emptyList(),
    val thumbnailUrl: String? = null,
    val albumId: String? = null,
    val albumTitle: String? = null,
    val duration: Int = -1,
)

fun MediaMetadata.toDownloadTarget() =
    DownloadTarget(
        id = id,
        title = title,
        artists = artists.map { it.name }.filter { it.isNotBlank() },
        thumbnailUrl = thumbnailUrl,
        albumId = album?.id,
        albumTitle = album?.title,
        duration = duration,
    )

fun Song.toDownloadTarget() = toMediaMetadata().toDownloadTarget()

private fun DownloadTarget.toRequestData(): ByteArray =
    JSONObject()
        .put("v", 1)
        .put("id", id)
        .put("title", title)
        .put("artists", JSONArray(artists))
        .put("thumbnailUrl", thumbnailUrl ?: JSONObject.NULL)
        .put("albumId", albumId ?: JSONObject.NULL)
        .put("albumTitle", albumTitle ?: JSONObject.NULL)
        .put("duration", duration)
        .toString()
        .toByteArray(Charsets.UTF_8)

/**
 * Backwards compatible decoder: old XAN builds stored only a plain UTF-8 title.
 */
fun decodeDownloadTarget(id: String, data: ByteArray): DownloadTarget {
    val raw = data.toString(Charsets.UTF_8).trim()
    if (raw.isBlank()) return DownloadTarget(id = id, title = "Unknown")

    return runCatching {
        val json = JSONObject(raw)
        val artistsJson = json.optJSONArray("artists")
        val artists =
            buildList {
                if (artistsJson != null) {
                    for (index in 0 until artistsJson.length()) {
                        artistsJson.optString(index)
                            .trim()
                            .takeIf { it.isNotEmpty() }
                            ?.let(::add)
                    }
                }
            }

        DownloadTarget(
            id = json.optString("id").takeIf { it.isNotBlank() } ?: id,
            title = json.optString("title").takeIf { it.isNotBlank() } ?: "Unknown",
            artists = artists,
            thumbnailUrl =
                json.optString("thumbnailUrl")
                    .takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) },
            albumId =
                json.optString("albumId")
                    .takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) },
            albumTitle =
                json.optString("albumTitle")
                    .takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) },
            duration = json.optInt("duration", -1),
        )
    }.getOrElse {
        DownloadTarget(id = id, title = raw)
    }
}

fun downloadDisplayTitle(download: Download): String =
    decodeDownloadTarget(download.request.id, download.request.data).title

@UnstableApi
fun buildDownloadRequest(target: DownloadTarget): DownloadRequest =
    DownloadRequest
        .Builder(target.id, target.id.toUri())
        .setCustomCacheKey(target.id)
        .setData(target.toRequestData())
        .build()

fun shouldQueueDownload(state: Int?): Boolean = when (state) {
    null, Download.STATE_FAILED, Download.STATE_STOPPED -> true
    else -> false
}

fun shouldCancelDownload(state: Int?): Boolean = when (state) {
    Download.STATE_QUEUED,
    Download.STATE_DOWNLOADING,
    Download.STATE_FAILED,
    Download.STATE_STOPPED,
    Download.STATE_RESTARTING,
    -> true

    else -> false
}

/** Queue songs while preserving their full display metadata in the request. */
@UnstableApi
fun downloadSongs(
    context: Context,
    songs: List<DownloadTarget>,
    downloads: Map<String, Download>,
) {
    songs.forEach { song ->
        if (!shouldQueueDownload(downloads[song.id]?.state)) return@forEach
        DownloadService.sendAddDownload(
            context,
            ExoDownloadService::class.java,
            buildDownloadRequest(song),
            false,
        )
    }
}

@UnstableApi
fun downloadSong(
    context: Context,
    target: DownloadTarget,
    downloads: Map<String, Download>,
) = downloadSongs(context, listOf(target), downloads)

/** Compatibility overload for call sites that only have id/title. */
@UnstableApi
fun downloadSong(
    context: Context,
    id: String,
    title: String,
    downloads: Map<String, Download>,
) = downloadSongs(context, listOf(DownloadTarget(id, title)), downloads)

@UnstableApi
fun cancelDownloads(
    context: Context,
    ids: List<String>,
    downloads: Map<String, Download>,
) {
    ids.forEach { id ->
        if (!shouldCancelDownload(downloads[id]?.state)) return@forEach
        DownloadService.sendRemoveDownload(context, ExoDownloadService::class.java, id, false)
    }
}

@UnstableApi
fun removeDownloads(
    context: Context,
    ids: List<String>,
) {
    ids.forEach { id ->
        DownloadService.sendRemoveDownload(context, ExoDownloadService::class.java, id, false)
    }
}
