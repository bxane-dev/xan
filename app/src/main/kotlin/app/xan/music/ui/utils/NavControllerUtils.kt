/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.ui.utils

import androidx.navigation.NavController
import app.xan.music.ui.screens.Screens

fun NavController.backToMain() {
    while (previousBackStackEntry != null &&
        currentBackStackEntry?.destination?.route !in Screens.MainRoutes
    ) {
        popBackStack()
    }
}
