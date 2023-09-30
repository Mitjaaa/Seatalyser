package de.bixilon.seatalyser

import com.fasterxml.jackson.annotation.JsonIgnore

class ReservierungsDaten(
    val carriage: List<Wagon>,
) {

    data class Wagon(
        val number: Int,
        val layout: String,
        val `class`: List<Int>,
        val seats: Map<Int, Seat>,
    ) {

        data class Seat(
            @JsonIgnore val number: Int,
            val `class`: Int,
            val status: SeatStatus,
        ) {

            enum class SeatStatus {
                RESERVED,
                AVAILABLE,
                UNKNOWN,
                ;

                companion object {

                    fun getById(id: Int): SeatStatus {
                        when (id) {
                            0 -> return RESERVED
                            1 -> return AVAILABLE
                            2 -> return AVAILABLE // that is our own place
                        }
                        System.err.println("Unknown seat status $id")
                        return UNKNOWN
                    }
                }
            }

        }
    }
}
