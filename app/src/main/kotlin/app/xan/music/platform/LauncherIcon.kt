package app.xan.music.platform

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/** Switches only the launcher alias; the APK installer keeps the app's dark icon. */
fun setInstalledAppIcon(context: Context, light: Boolean) {
    val packageManager = context.packageManager
    val darkAlias = ComponentName(context.packageName, "app.xan.music.launcher.DefaultIcon")
    val lightAlias = ComponentName(context.packageName, "app.xan.music.launcher.LightIcon")

    fun enabled(alias: ComponentName, manifestDefault: Boolean): Boolean =
        when (packageManager.getComponentEnabledSetting(alias)) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> manifestDefault
            else -> false
        }

    if (enabled(darkAlias, manifestDefault = true) == !light &&
        enabled(lightAlias, manifestDefault = false) == light
    ) return

    val enable = if (light) lightAlias else darkAlias
    val disable = if (light) darkAlias else lightAlias
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.setComponentEnabledSettings(
            listOf(
                PackageManager.ComponentEnabledSetting(
                    enable,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP,
                ),
                PackageManager.ComponentEnabledSetting(
                    disable,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP,
                ),
            ),
        )
    } else {
        // Enable the next alias first, so there is always a launcher entry.
        packageManager.setComponentEnabledSetting(
            enable,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
        packageManager.setComponentEnabledSetting(
            disable,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
    }
}
