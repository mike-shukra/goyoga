package ru.yogago.goyoga.data

data class Data(
    val userData: UserData? = null,
    val asanas: List<Asana>? = null,
    val error: String = "no"
)
