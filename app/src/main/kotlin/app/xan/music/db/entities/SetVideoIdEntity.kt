/**
 * xan Project (C) 2026
 * Licensed under MIT | See LICENCE and git history for contributors
 */

package app.xan.music.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "set_video_id")
data class SetVideoIdEntity(
    @PrimaryKey(autoGenerate = false)
    val videoId: String = "",
    val setVideoId: String? = null,
)
