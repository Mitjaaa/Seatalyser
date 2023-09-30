package de.bixilon.seatalyser.dto

data class Train(
    val type: TrainTypes,
    val id: Int,
    val stops: List<Int>,
) {

    enum class TrainTypes {
        ICE,
        IC,
        EC,
        ;
    }
}
