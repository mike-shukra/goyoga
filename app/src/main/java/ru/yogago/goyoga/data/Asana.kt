package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Asana (
    @PrimaryKey val id: Long,
    val name: String,
    val eng: String,
    val description: String,
    val description_en: String,
    val photo: String,
    val symmetric: String,
    val side: String,
    var times: Int
)