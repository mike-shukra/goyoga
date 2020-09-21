package ru.yogago.goyoga.data

data class Data(
    val userData: UserData,
    val asanas: List<Asana>,
    val error: String = "no"
)
