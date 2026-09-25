/**
 * xan Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI ported from vivizzz007/vivi-music (https://github.com/vivizzz007/vivi-music), GPL-3.0.

package app.xan.music.ui.player

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import app.xan.music.canvas.CanvasArtwork
import app.xan.music.canvas.TidalCanvasProvider
import app.xan.music.applecanvas.AppleMusicCanvasProvider
import app.xan.music.xancanvas.XanCanvasProvider
import app.xan.music.constants.CanvasSource
import app.xan.music.constants.CanvasSourceKey
import app.xan.music.models.MediaMetadata
import app.xan.music.utils.rememberEnumPreference
import app.xan.music.utils.rememberPreference
import app.xan.music.constants.CanvasThumbnailAnimationKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun PlayerV2Canvas(
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    if (mediaMetadata == null) return

    val enableCanvas by rememberPreference(CanvasThumbnailAnimationKey, defaultValue = true)
    if (!enableCanvas) return

    val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)
    val albumTitle = mediaMetadata.album?.title
    var canvasArtwork by remember(mediaMetadata.id, albumTitle) { mutableStateOf<CanvasArtwork?>(null) }
    var canvasFetchInFlight by remember(mediaMetadata.id, albumTitle) { mutableStateOf(false) }
    
    val storefront = remember {
        val country = Locale.getDefault().country
        if (country.length == 2) country.lowercase(Locale.ROOT) else "us"
    }

    LaunchedEffect(mediaMetadata.id, albumTitle, canvasSource) {
        val cacheKey = "${mediaMetadata.id}:${canvasSource.name}"
        CanvasArtworkPlaybackCache.get(cacheKey)?.let { cached ->
            canvasArtwork = cached
            return@LaunchedEffect
        }

        if (canvasFetchInFlight) return@LaunchedEffect
        canvasFetchInFlight = true

        val fetched = withContext(Dispatchers.IO) {
            val songTitle = mediaMetadata.title ?: ""
            val artistName = mediaMetadata.artists.firstOrNull()?.name ?: ""
            val albumName = albumTitle ?: ""

            when (canvasSource) {
                CanvasSource.AUTO -> {
                    AppleMusicCanvasProvider.getBySongArtist(songTitle, artistName, albumName, storefront)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                        ?: TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                            ?.takeIf { !it.animated.isNullOrBlank() }
                        ?: XanCanvasProvider.getBySongArtist(songTitle, artistName)
                            ?.takeIf { !it.animated.isNullOrBlank() }
                }
                CanvasSource.APPLE_MUSIC -> {
                    AppleMusicCanvasProvider.getBySongArtist(songTitle, artistName, albumName, storefront)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                }
                CanvasSource.XAN -> {
                    XanCanvasProvider.getBySongArtist(songTitle, artistName)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                }
                CanvasSource.TIDAL -> {
                    TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                }
                else -> null
            }
        }

        if (fetched != null) {
            canvasArtwork = fetched
            CanvasArtworkPlaybackCache.put(cacheKey, fetched)
        }
        canvasFetchInFlight = false
    }

    canvasArtwork?.let { artwork ->
        CanvasArtworkPlayer(
            primaryUrl = artwork.preferredAnimationUrl,
            fallbackUrl = artwork.animated,
            isPlaying = isPlaying,
            modifier = modifier
        )
    }
}
