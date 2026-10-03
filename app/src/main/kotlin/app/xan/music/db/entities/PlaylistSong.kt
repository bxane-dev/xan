/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.db.entities

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation

@Immutable
data class PlaylistSong(
    @Embedded val map: PlaylistSongMap,
    @Relation(
        parentColumn = "songId",
        entityColumn = "id",
        entity = SongEntity::class,
    )
    val song: Song,
)
