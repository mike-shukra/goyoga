package ru.yogago.goyoga.data

data class Data(
    var userData: UserData? = null,
    var asanas: List<Asana>? = null,
    var actionState: ActionState? = null,
    var settings: Settings? = null,
    var error: String = "no"
)
