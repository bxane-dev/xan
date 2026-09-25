package app.xan.music.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Canonical project links.
 *
 * Discord is intentionally sourced from README.md at runtime so the invite can
 * be changed on GitHub without shipping a new APK. DISCORD_FALLBACK is used
 * when the README cannot be reached (for example while the repository is private).
 */
object ProjectLinks {
    const val REPOSITORY = "https://github.com/bxane-dev/xan"
    const val RELEASES = "$REPOSITORY/releases"
    const val DISCORD_FALLBACK = "https://discord.gg/h4Wsu824Uf"
    const val LEAD_DEVELOPER = "https://github.com/bxane-dev"

    private const val README_RAW =
        "https://raw.githubusercontent.com/bxane-dev/xan/main/README.md"

    suspend fun discordUrl(): String = withContext(Dispatchers.IO) {
        val connection = runCatching {
            (URL(README_RAW).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 4_000
                readTimeout = 4_000
                setRequestProperty("User-Agent", "xan-android")
            }
        }.getOrNull() ?: return@withContext DISCORD_FALLBACK

        try {
            if (connection.responseCode !in 200..299) return@withContext DISCORD_FALLBACK
            val readme = connection.inputStream.bufferedReader().use { it.readText() }
            Regex("""https://discord\.gg/[A-Za-z0-9_-]+""")
                .find(readme)
                ?.value
                ?: DISCORD_FALLBACK
        } catch (_: Exception) {
            DISCORD_FALLBACK
        } finally {
            connection.disconnect()
        }
    }
}
