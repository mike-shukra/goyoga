package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Token(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    val token: String = "no",
    val userId: Long? = null,
    val message: String? = null,
    val error: String? = null
)