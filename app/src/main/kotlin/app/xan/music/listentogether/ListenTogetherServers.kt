/**
 * xan Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package app.xan.music.listentogether

import kotlinx.serialization.Serializable

@Serializable
data class ListenTogetherServer(
    val name: String,
    val url: String,
    val location: String,
    val operator: String
)

object ListenTogetherServers {
    // XAN has no operated public sync server. Users can enter their own in settings.
    val servers: List<ListenTogetherServer> = emptyList()

    val defaultServerUrl: String = ""

    fun findByUrl(url: String): ListenTogetherServer? = servers.firstOrNull { it.url == url }
}
