/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import app.xan.music.utils.NetworkConnectivityObserver

@Composable
fun NetworkReload(
    onReload: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        val observer = NetworkConnectivityObserver(context.applicationContext)
        var wasOffline = false
        try {
            observer.networkStatus.collect { isConnected ->
                if (isConnected) {
                    if (wasOffline) {
                        onReload()
                    }
                    wasOffline = false
                } else {
                    wasOffline = true
                }
            }
        } finally {
            observer.unregister()
        }
    }
}
