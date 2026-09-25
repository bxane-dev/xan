/**
 * xan Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package app.xan.music.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.xan.music.BuildConfig
import app.xan.music.LocalPlayerAwareWindowInsets
import app.xan.music.R
import app.xan.music.constants.SupporterUrl
import app.xan.music.platform.ProjectLinks
import app.xan.music.ui.component.IconButton
import app.xan.music.ui.component.Material3SettingsGroup
import app.xan.music.ui.component.Material3SettingsItem
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.utils.backToMain
import app.xan.music.ui.utils.safeOpenUri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val unknownString = stringResource(R.string.unknown)
    var discordUrl by remember { mutableStateOf(ProjectLinks.DISCORD_FALLBACK) }
    LaunchedEffect(Unit) { discordUrl = ProjectLinks.discordUrl() }

    val installedDate = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(Date(packageInfo.firstInstallTime))
        } catch (_: Exception) {
            unknownString
        }
    }

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.xan_mark),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.XAN_title),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    letterSpacing = 2.sp,
                ),
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = CircleShape,
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Material3SettingsGroup(
            title = stringResource(R.string.developer_section),
            items = listOf(
                Material3SettingsItem(
                    leadingContent = {
                        Image(
                            painter = painterResource(R.drawable.bxane_profile),
                            contentDescription = "bxane profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                        )
                    },
                    title = { Text("bxane") },
                    description = { Text("Lead developer", color = MaterialTheme.colorScheme.primary) },
                    onClick = { uriHandler.safeOpenUri(context, ProjectLinks.LEAD_DEVELOPER) },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.discord),
                    title = { Text(stringResource(R.string.discord_channel)) },
                    description = { Text(stringResource(R.string.join_discord)) },
                    onClick = { uriHandler.safeOpenUri(context, discordUrl) },
                ),
            ),
        )

        Spacer(Modifier.height(24.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.community_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.github),
                    title = { Text(stringResource(R.string.github_repository)) },
                    description = { Text(stringResource(R.string.view_source_code)) },
                    onClick = { uriHandler.safeOpenUri(context, ProjectLinks.REPOSITORY) },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.changelog_paper),
                    title = { Text("Changelog") },
                    description = { Text("Release notes from GitHub Releases") },
                    onClick = { navController.navigate("settings/changelog") },
                ),
            ),
        )

        Spacer(Modifier.height(24.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.support_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.favorite),
                    title = { Text("Support bxane") },
                    description = { Text("Open the supporter page") },
                    onClick = { uriHandler.safeOpenUri(context, SupporterUrl) },
                ),
            ),
        )

        Spacer(Modifier.height(24.dp))

        Material3SettingsGroup(
            title = stringResource(R.string.app_info_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.deployed_app_update),
                    title = { Text(stringResource(R.string.installed_date_title)) },
                    description = { Text(installedDate) },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text("Release") },
                    description = { Text(BuildConfig.VERSION_NAME) },
                    onClick = { uriHandler.safeOpenUri(context, ProjectLinks.RELEASES) },
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.license_xan),
                    title = { Text(stringResource(R.string.license)) },
                    description = { Text("GPL-3.0 • Free Open Source Software") },
                    onClick = {
                        uriHandler.safeOpenUri(
                            context,
                            ProjectLinks.REPOSITORY + "/blob/main/LICENSE",
                        )
                    },
                ),
            ),
        )

        Spacer(Modifier.height(20.dp))
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.about)) },
        navigationIcon = {
            IconButton(
                onClick = { onBack?.invoke() ?: navController.navigateUp() },
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
