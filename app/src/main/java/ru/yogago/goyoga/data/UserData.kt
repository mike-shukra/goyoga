package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData (
    @PrimaryKey val id: Long,
    val email: String?,
    var first_name: String = "default",
    val now: Int? = 1,
    val allTime: Int? = 0,
    val allCount: Int? = 0,
    val level: Int? = 0,
    val dangerknee: Int? = 0,
    val dangerloins: Int? = 0,
    val dangerneck: Int? = 0,
    val date: String? = ""
)