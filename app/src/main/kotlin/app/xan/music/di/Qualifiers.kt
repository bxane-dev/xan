/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlayerCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
