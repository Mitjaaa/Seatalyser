package de.bixilon.seatalyser

import de.bixilon.kutil.json.JsonObject
import de.bixilon.kutil.primitive.IntUtil.toInt

object ReserveriungsParser {

    private fun Data.readSeat(dataIndex: Int): ReservierungsDaten.Wagon.Seat {
        val data = this[dataIndex] as JsonObject
        val status = this[data["status"] as Int].toInt()
        val number = this[data["nummer"] as Int].toInt()

        return ReservierungsDaten.Wagon.Seat(number, 0, ReservierungsDaten.Wagon.Seat.SeatStatus.getById(status))
    }

    private fun List<Any>.readWagon(dataIndex: Int): ReservierungsDaten.Wagon {
        val data = this[dataIndex] as JsonObject
        val plaetze = this[data["plaetze"] as Int] as List<Int>
        val number = this[data["nummer"] as Int].toInt()
        val layout = this[data["wagentyp"] as Int].toString()

        val seats: MutableMap<Int, ReservierungsDaten.Wagon.Seat> = mutableMapOf()

        for (platz in plaetze) {
            val seat = readSeat(platz)
            seats[seat.number] = seat
        }


        return ReservierungsDaten.Wagon(number, layout, ReservierungsDaten.Wagon.WagonType.WAGON, listOf(0), seats)
    }

    fun parse(data: List<Any>): ReservierungsDaten {
        val wagons: MutableList<ReservierungsDaten.Wagon> = mutableListOf()
        for (index in (data[32] as List<Int>)) {
            wagons += data.readWagon(index)
        }

        return ReservierungsDaten(wagons)
    }
}

typealias Data = List<Any>
