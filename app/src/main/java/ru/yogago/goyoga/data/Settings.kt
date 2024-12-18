package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings (
    @PrimaryKey var id: Long = 0,
    var language: String = "en",
    var proportionately: Float = 1F,
    var addTime: Int = 0,
    var speakAsanaName: Boolean = true,
    var timeOfFiltered: Long = 0
)