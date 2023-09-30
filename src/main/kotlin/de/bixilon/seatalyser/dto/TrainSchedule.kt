package de.bixilon.seatalyser.dto

data class TrainSchedule(
    val train: Int,
    val stops: List<TrainRoute>,
)
