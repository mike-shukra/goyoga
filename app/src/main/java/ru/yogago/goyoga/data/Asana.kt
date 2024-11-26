package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Asana (
    @PrimaryKey var id: Long,
    var name: String,
    var eng: String,
    var description: String,
    var description_en: String,
    var photo: String,
    var symmetric: String,
    var side: String,
    var times: Int
)