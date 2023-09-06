package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Token(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0,
    var token: String = "no",
    var userId: Long? = null,
    var message: String? = null,
    var error: String? = null
)