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
        now = actionState.currentId,
        allTime = userData.allTime,
        allCount = userData.allCount,
        level = userData.level.toString(),
        proportionally = settings.proportionately,
        addTime = settings.addTime,
        dangerKnee = userData.dangerknee,
        dangerLoins = userData.dangerloins,
        dangerNeck = userData.dangerneck,
        inverted = userData.inverted,
        sideBySideSort = userData.sideBySideSort,
        timeOfFiltered = settings.timeOfFiltered
    )
}
