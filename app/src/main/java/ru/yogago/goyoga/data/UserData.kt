package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData (
    @PrimaryKey val id: Long,
    val email: String?,
    var first_name: String = "default",
    val now: Int?,
    val allTime: Int?,
    val allCount: Int?,
    val level: String?,
    val dangerknee: Int?,
    val dangerloins: Int?,
    val dangerneck: Int?,
    val date: String?,
)