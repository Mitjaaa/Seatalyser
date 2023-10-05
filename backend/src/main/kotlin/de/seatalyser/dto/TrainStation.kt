package de.seatalyser.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TrainStation(
    @JsonProperty("_id") val id: Int,
    val name: String,
)
