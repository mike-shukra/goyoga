package ru.yogago.goyoga.data

data class ParametersDTO(
    var now: Int? = 1,
    var allTime: Int? = 0,
    var allCount: Int? = 0,
    var level: String? = "NOT_SPECIFIED",
    var proportionally: Float?,
    var addTime: Int?,
    var dangerKnee: Boolean?,
    var dangerLoins: Boolean?,
    var dangerNeck: Boolean?,
    var inverted: Boolean?,
    var sideBySideSort: Boolean,
    var timeOfFiltered: Long
) {
    constructor(actionState: ActionState, userData: UserData, settings: Settings) : this(
        actionState.currentId,
        userData.allTime,
        userData.allCount,
        userData.level.toString(),
        settings.proportionately,
        settings.addTime,
        userData.dangerknee,
        userData.dangerloins,
        userData.dangerneck,
        userData.inverted,
        userData.sideBySideSort,
        settings.timeOfFiltered
    )
}
