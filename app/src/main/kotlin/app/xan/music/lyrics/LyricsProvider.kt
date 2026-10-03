/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.lyrics

import android.content.Context

interface LyricsProvider {
    val name: String

    fun isEnabled(context: Context): Boolean

    suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String? = null,
    ): Result<String>

    suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String? = null,
        callback: (String) -> Unit,
    ) {
        getLyrics(id, title, artist, duration, album).onSuccess(callback)
    }
}
