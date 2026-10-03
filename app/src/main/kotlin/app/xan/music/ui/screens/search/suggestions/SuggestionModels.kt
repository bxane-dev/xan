/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.ui.screens.search.suggestions

data class SuggestionTrack(
    val rank: Int,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val appleMusicUrl: String? = null
)

data class SuggestionArtist(
    val rank: Int,
    val name: String,
    val thumbnailUrl: String?
)

data class SuggestionAlbum(
    val rank: Int,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val appleMusicUrl: String? = null
)
