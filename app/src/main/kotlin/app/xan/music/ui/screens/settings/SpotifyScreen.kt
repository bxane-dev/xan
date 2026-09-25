package app.xan.music.ui.screens.settings

import android.content.Intent
import android.net.Uri
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.utils.appTopBarWindowInsets
import android.webkit.CookieManager
import android.webkit.WebResourceError
import app.xan.music.ui.utils.appTopBarWindowInsets
import android.webkit.WebResourceRequest
import app.xan.music.ui.utils.appTopBarWindowInsets
import android.webkit.WebView
import app.xan.music.ui.utils.appTopBarWindowInsets
import android.webkit.WebViewClient
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.AnimatedVisibility
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.animateColorAsState
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.LinearEasing
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.RepeatMode
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.animateFloat
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.infiniteRepeatable
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.rememberInfiniteTransition
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.tween
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.background
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.graphicsLayer
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.*
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.items
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.CircleShape
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.*
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.*
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.draw.clip
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.Color
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.layout.ContentScale
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.platform.LocalContext
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.font.FontWeight
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.text.style.TextOverflow
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.viewinterop.AndroidView
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.hilt.navigation.compose.hiltViewModel
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import app.xan.music.ui.utils.appTopBarWindowInsets
import coil3.compose.AsyncImage
import app.xan.music.ui.utils.appTopBarWindowInsets
import com.music.spotify.SpotifyAuth
import app.xan.music.ui.utils.appTopBarWindowInsets
import com.music.spotify.SpotifyMapper
import app.xan.music.ui.utils.appTopBarWindowInsets
import com.music.spotify.models.SpotifyPlaylist
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.LocalPlayerAwareWindowInsets
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.R
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.DefaultDialog
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.IconButton
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.Material3SettingsGroup
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.Material3SettingsItem
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.menu.LoadingScreen
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.utils.backToMain
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.utils.rememberPreference
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.viewmodels.SpotifyImportViewModel
import app.xan.music.ui.utils.appTopBarWindowInsets
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    autoStartLogin: Boolean = false,
    viewModel: SpotifyImportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showSpotifyLogin by remember { mutableStateOf(false) }
    var showPlaylistsSheet by remember { mutableStateOf(false) }
    val importProgress by viewModel.importProgress.collectAsStateWithLifecycle()

    LaunchedEffect(autoStartLogin, state.isAuthenticated) {
        if (autoStartLogin && !state.isAuthenticated) {
            showSpotifyLogin = true
        }
    }

    val refreshEnabled = state.isAuthenticated && !state.isLoading
    val rotationAngle by if (state.isLoading) {
        val transition = rememberInfiniteTransition(label = "rotation")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotationAngle"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        Text(
            text = stringResource(R.string.spotify),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp, top = 24.dp, bottom = 16.dp)
        )

        // Connection Card/Group
        Material3SettingsGroup(
            title = stringResource(R.string.spotify_account),
            items = listOf(
                if (state.isAuthenticated) {
                    Material3SettingsItem(
                        leadingContent = if (!state.accountAvatarUrl.isNullOrBlank()) {
                            {
                                AsyncImage(
                                    model = state.accountAvatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else null,
                        icon = if (state.accountAvatarUrl.isNullOrBlank()) painterResource(R.drawable.spotify) else null,
                        title = {
                            Text(
                                text = if (state.accountName.isNotBlank()) state.accountName
                                else stringResource(R.string.spotify_account),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        trailingContent = {
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(stringResource(R.string.action_logout))
                            }
                        },
                        onClick = {}
                    )
                } else {
                    Material3SettingsItem(
                        title = { Text("Spotify Web") },
                        description = { Text("Sign in with Spotify Web") },
                        icon = painterResource(R.drawable.spotify),
                        onClick = { showSpotifyLogin = true }
                    )
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        val totalPlaylists = state.playlists.size + if (state.likedSongsCount > 0) 1 else 0
        Material3SettingsGroup(
            title = stringResource(R.string.playlists),
            items = listOf(
                Material3SettingsItem(
                    title = { Text(stringResource(R.string.spotify_select_sources)) },
                    description = {
                        Text(
                            if (state.isAuthenticated) {
                                if (totalPlaylists > 0) stringResource(R.string.spotify_available_count, totalPlaylists)
                                else stringResource(R.string.spotify_no_sources)
                            } else {
                                stringResource(R.string.spotify_not_connected)
                            }
                        )
                    },
                    icon = painterResource(R.drawable.bookmark_star_library),
                    enabled = state.isAuthenticated && totalPlaylists > 0 && !state.isLoading,
                    onClick = { showPlaylistsSheet = true }
                ),
                Material3SettingsItem(
                    title = { Text(stringResource(R.string.spotify_refresh)) },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.sync),
                                contentDescription = null,
                                tint = if (!refreshEnabled) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        rotationZ = rotationAngle
                                    }
                            )
                        }
                    },
                    enabled = refreshEnabled,
                    onClick = { viewModel.loadSources() }
                )
            )
        )

        // Info block
        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.spotify_import_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = {},
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )

    if (showSpotifyLogin) {
        SpotifyLoginSheet(
            onDismiss = { showSpotifyLogin = false },
            onCookiesCaptured = { spDc, spKey ->
                showSpotifyLogin = false
                viewModel.connectWithCookies(spDc, spKey)
            }
        )
    }

    importProgress?.let { progress ->
        val isFinished = progress.isFinished || progress.percent >= 1f
        DefaultDialog(
            onDismiss = {
                if (isFinished) {
                    viewModel.dismissImportProgress()
                } else {
                    viewModel.cancelImport()
                }
            },
            title = {
                Text(
                    text = if (isFinished) {
                        stringResource(R.string.spotify_import_complete)
                    } else {
                        stringResource(R.string.spotify_import_in_progress)
                    }
                )
            },
            buttons = {
                if (isFinished) {
                    Button(
                        onClick = { viewModel.dismissImportProgress() },
                        shape = CircleShape
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                } else {
                    TextButton(onClick = { viewModel.cancelImport() }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.playlists) + ": " + progress.playlistName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isFinished) {
                        stringResource(R.string.spotify_imported_songs_count, progress.currentSongIndex, progress.totalSongs)
                    } else {
                        stringResource(R.string.spotify_importing_songs_count, progress.currentSongIndex, progress.totalSongs)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = { progress.percent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
    state.errorMessage?.let { error ->
        DefaultDialog(
            onDismiss = { viewModel.dismissError() },
            title = { Text("Error") },
            buttons = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showPlaylistsSheet) {
        SpotifyPlaylistBottomSheet(
            onDismiss = { showPlaylistsSheet = false },
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpotifyLoginSheet(
    onDismiss: () -> Unit,
    onCookiesCaptured: (spDc: String, spKey: String) -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var captured by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var pageError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            webView?.destroy()
            webView = null
        }
    }

    // A real window-style login surface instead of a bottom sheet. This keeps the
    // Spotify web session visible and usable like the dedicated login windows in
    // other music clients, especially on tablets and landscape devices.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.spotify_login_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.spotify_waiting_for_login),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp)),
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                val cookieManager = CookieManager.getInstance()
                                cookieManager.setAcceptCookie(true)
                                cookieManager.setAcceptThirdPartyCookies(this, true)
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.setSupportZoom(true)
                                settings.builtInZoomControls = true
                                settings.displayZoomControls = false
                                settings.javaScriptCanOpenWindowsAutomatically = true
                                settings.setSupportMultipleWindows(false)
                                settings.mediaPlaybackRequiresUserGesture = false
                                // Spotify serves a degraded login page to the Android WebView UA.
                                settings.userAgentString =
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                        "Chrome/131.0.0.0 Safari/537.36"
                                webViewClient = object : WebViewClient() {
                                    private fun captureCookies(url: String?): Boolean {
                                        if (captured) return true
                                        cookieManager.flush()
                                        val cookiesStr =
                                            cookieManager.getCookie("https://open.spotify.com") ?: ""
                                        val cookies = cookiesStr.split(";").mapNotNull { cookie ->
                                            val parts = cookie.trim().split("=", limit = 2)
                                            if (parts.size != 2) null else parts[0] to parts[1]
                                        }.toMap()
                                        val spDc = cookies["sp_dc"].orEmpty()
                                        if (spDc.isBlank()) return false
                                        captured = true
                                        onCookiesCaptured(spDc, cookies["sp_key"].orEmpty())
                                        return true
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest,
                                    ): Boolean {
                                        val uri = request.url
                                        if (uri.scheme !in listOf("http", "https")) {
                                            view.context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                            return true
                                        }
                                        // Let WebView continue normal Spotify navigation. Capturing
                                        // cookies must not cancel the page transition.
                                        captureCookies(uri.toString())
                                        return false
                                    }

                                    override fun onPageStarted(
                                        view: WebView,
                                        url: String?,
                                        favicon: android.graphics.Bitmap?,
                                    ) {
                                        isLoading = true
                                        pageError = null
                                        captureCookies(url)
                                    }

                                    override fun onPageFinished(view: WebView, url: String?) {
                                        isLoading = false
                                        captureCookies(url)
                                    }

                                    override fun onReceivedError(
                                        view: WebView,
                                        request: WebResourceRequest,
                                        error: WebResourceError,
                                    ) {
                                        if (request.isForMainFrame) {
                                            isLoading = false
                                            pageError = error.description?.toString()
                                                ?: "Spotify could not be loaded."
                                        }
                                    }
                                }
                                webView = this
                                cookieManager.removeAllCookies {
                                    cookieManager.flush()
                                    post { loadUrl(SpotifyAuth.LOGIN_URL) }
                                }
                            }
                        },
                        update = { view -> webView = view },
                    )

                    if (isLoading && pageError == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    pageError?.let { message ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                pageError = null
                                isLoading = true
                                webView?.reload()
                            }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}
