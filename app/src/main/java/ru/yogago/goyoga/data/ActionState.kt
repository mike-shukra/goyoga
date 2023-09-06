package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActionState(
    @PrimaryKey var id: Long = 0,
    var currentId: Int = 0,
    var isPlay: Boolean = false
)