package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings (
    @PrimaryKey val id: Long = 0,
    val language: String = "",
    val proportionately: Float = 1F,
    val addTime: Int = 0,
    val isSpeakAsanaName: Boolean = false
)