package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Asana (
    @PrimaryKey val id: Long,
    val name: String,
    val photo: String,
    val symmetric: String,
    val side: String,
    val times: Int
)