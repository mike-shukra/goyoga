package ru.yogago.goyoga.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserData (
    @PrimaryKey var id: Long,
    var email: String? = null,
    var first_name: String? = null,
    var now: Int = 1,
    var allTime: Int = 0,
    var allCount: Int = 0,
    var level: Int = 0,
    var dangerknee: Int = 0,
    var dangerloins: Int = 0,
    var dangerneck: Int = 0,
    var inverted: Int = 0,
    var date: String? = null
)