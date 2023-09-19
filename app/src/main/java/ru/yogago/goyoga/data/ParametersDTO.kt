package ru.yogago.goyoga.data

data class ParametersDTO(
    var now: Int? = 1,
    var allTime: Int? = 0,
    var allCount: Int? = 0,
    var level: String? = "NOT_SPECIFIED",
    var dangerKnee: Boolean?,
    var dangerLoins: Boolean?,
    var dangerNeck: Boolean?,
    var inverted: Boolean?
)
