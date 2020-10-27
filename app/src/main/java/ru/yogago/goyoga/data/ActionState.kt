package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActionState(
    @PrimaryKey val id: Long = 0,
    var currentId: Int = 1,
    var isPlay: Boolean = false
)