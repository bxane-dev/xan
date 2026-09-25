package app.xan.music.platform.github

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class GitHubReleaseAsset(
    val name: String,
    val sizeBytes: Long,
    val downloadUrl: String,
)

data class GitHubRelease(
    val tagName: String,
    val title: String,
    val body: String,
    val publishedAt: String,
    val prerelease: Boolean,
    val htmlUrl: String,
    val assets: List<GitHubReleaseAsset>,
) {
    val displayDate: String
        get() = runCatching {
            OffsetDateTime.parse(publishedAt)
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()))
        }.getOrElse { publishedAt }
}

object GitHubReleases {
    private const val API_URL = "https://api.github.com/repos/bxane-dev/xan/releases?per_page=100"

    suspend fun fetch(includePrereleases: Boolean): Result<List<GitHubRelease>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val connection = URL(API_URL).openConnection() as HttpURLConnection
                try {
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 12_000
                    connection.readTimeout = 12_000
                    connection.setRequestProperty("Accept", "application/vnd.github+json")
                    connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
                    connection.setRequestProperty("User-Agent", "xan-android")

                    val responseCode = connection.responseCode
                    if (responseCode !in 200..299) {
                        val body = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                        error("GitHub Releases request failed (" + responseCode + "): " + body)
                    }

                    val payload = connection.inputStream.bufferedReader().use { it.readText() }
                    val array = JSONArray(payload)
                    buildList {
                        for (index in 0 until array.length()) {
                            val release = array.getJSONObject(index)
                            if (release.optBoolean("draft", false)) continue

                            val prerelease = release.optBoolean("prerelease", false)
                            if (!includePrereleases && prerelease) continue

                            val assetsJson = release.optJSONArray("assets") ?: JSONArray()
                            val assets = buildList {
                                for (assetIndex in 0 until assetsJson.length()) {
                                    val asset = assetsJson.getJSONObject(assetIndex)
                                    add(
                                        GitHubReleaseAsset(
                                            name = asset.optString("name"),
                                            sizeBytes = asset.optLong("size"),
                                            downloadUrl = asset.optString("browser_download_url"),
                                        )
                                    )
                                }
                            }

                            val tag = release.optString("tag_name")
                            add(
                                GitHubRelease(
                                    tagName = tag,
                                    title = release.optString("name").ifBlank { tag },
                                    body = release.optString("body"),
                                    publishedAt = release.optString("published_at")
                                        .ifBlank { release.optString("created_at") },
                                    prerelease = prerelease,
                                    htmlUrl = release.optString("html_url"),
                                    assets = assets,
                                )
                            )
                        }
                    }
                } finally {
                    connection.disconnect()
                }
            }
        }
}
