/**
 * xan Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package app.xan.music.ui.screens.artist

import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.activity.compose.BackHandler
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.GridItemSpan
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.items
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHost
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHostState
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.collectAsState
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateListOf
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.rememberCoroutineScope
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.listSaver
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.toMutableStateList
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.pluralStringResource
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.xan.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.LocalPlayerAwareWindowInsets
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.LocalPlayerConnection
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.R
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.utils.rememberGridColumns
import app.xan.music.constants.CONTENT_TYPE_ALBUM
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.constants.CONTENT_TYPE_HEADER
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.constants.GridItemSize
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.constants.GridItemsSizeKey
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.constants.GridThumbnailHeight
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.ListScrollRail
import app.xan.music.ui.component.IconButton
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.LibraryAlbumGridItem
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.component.LocalMenuState
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.ui.utils.backToMain
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.utils.rememberEnumPreference
import app.xan.music.ui.utils.appTopBarWindowInsets
import app.xan.music.viewmodels.ArtistAlbumsViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistAlbumsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistAlbumsViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val artist by viewModel.artist.collectAsState()
    val albums by viewModel.albums.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<String>, String>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }
    if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val visibleAlbums = remember(albums) { albums.distinctBy { it.id } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = rememberGridColumns(),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
        ) {
            item(
                key = "header",
                span = { GridItemSpan(maxLineSpan) },
                contentType = CONTENT_TYPE_HEADER
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = pluralStringResource(R.plurals.n_album, albums.size, albums.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            items(
                items = visibleAlbums,
                key = { it.id },
                contentType = { CONTENT_TYPE_ALBUM }
            ) { album ->
                LibraryAlbumGridItem(
                    navController = navController,
                    menuState = menuState,
                    coroutineScope = coroutineScope,
                    album = album,
                    isActive = album.id == mediaMetadata?.album?.id,
                    isPlaying = isPlaying,
                    modifier = Modifier.animateItem()
                )
            }
        }

        // No sort control on this screen -- albums arrive in release order -- so the rail
        // is a proportional thumb rather than letters.
        ListScrollRail(
            lazyGridState = lazyGridState,
            itemCount = visibleAlbums.size,
            sectionIndexMap = null,
        )

        TopAppBar(
            windowInsets = appTopBarWindowInsets(),
            title = { Text(artist?.artist?.name.orEmpty()) },
            navigationIcon = {
                IconButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .align(Alignment.BottomCenter)
        )
    }
}
