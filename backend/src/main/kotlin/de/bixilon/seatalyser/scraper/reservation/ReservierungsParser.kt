package de.bixilon.seatalyser.scraper.reservation

import com.fasterxml.jackson.module.kotlin.readValue
import de.bixilon.kutil.cast.CastUtil.unsafeCast
import de.bixilon.kutil.json.JsonObject
import de.bixilon.kutil.primitive.IntUtil.toInt
import de.bixilon.seatalyser.MAPPER
import de.bixilon.seatalyser.dto.Train
import de.bixilon.seatalyser.dto.TrainRoute
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


object ReservierungsParser {
    val ABFAHRT_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val TIMEZONE = ZoneId.of("Europe/Berlin")


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


        return ReservierungsDaten.Wagon(number, layout, listOf(0), seats)
    }

    fun parse(data: List<Any>): ReservierungsDaten {
        // [2] -> parsedDataAndZugfahrt -> zugfahrt -> zugteile -> [for each] -> wagen -> [for each] -> plaetze -> [for each]
        val parsed = data[2].unsafeCast<JsonObject>()["parsedDataAndZugfahrt"].toInt()
        val zugfahrt = data[parsed].unsafeCast<JsonObject>()["zugfahrt"].toInt()

        val teileIndex = data[zugfahrt].unsafeCast<JsonObject>()["zugteile"].toInt()


        val wagons: MutableList<ReservierungsDaten.Wagon> = mutableListOf()

        for (teilIndex in data[teileIndex] as List<Int>) {
            val teil = data[teilIndex].unsafeCast<JsonObject>()
            val wagenIndex = teil["wagen"].toInt()

            for (index in (data[wagenIndex] as List<Int>)) {
                wagons += data.readWagon(index)
            }
        }



        return ReservierungsDaten(wagons)
    }


    private fun Int.prependDate(): String {
        val string = toString()
        if (string.length == 2) return string
        return "0".repeat(2 - string.length) + string
    }


    fun buildEpa(id: Int, departure: ZonedDateTime): String {
        return "EPA#${id}_${departure.year}-${departure.monthValue.prependDate()}-${departure.dayOfMonth.prependDate()}" // "EPA#803_2023-10-03
    }

    fun buildAbfahrt(date: Instant): String {
        return ABFAHRT_FORMAT.format(Date.from(date))
    }

    fun createQuery(train: Train, route: TrainRoute, `class`: Int): ReservierungsQuery {
        val departure = ZonedDateTime.ofInstant(route.departure, TIMEZONE)


        val key = "EPA#${train.id}_${departure.year}-${departure.monthValue}-${departure.dayOfMonth}" // "EPA#803_2023-10-03
        val abfahrt = ReservierungsQuery.Buchungskontext.Kontext.Abfahrt(route.start, buildAbfahrt(route.departure))
        val ankunft = ReservierungsQuery.Buchungskontext.Kontext.Ankunft(route.end)
        val klasse = ReservierungsQuery.Buchungskontext.Kontext.KategorieCode.byClass(`class`)

        val kontext = ReservierungsQuery.Buchungskontext.Kontext(
            zugnummer = train.id,
            zugfahrtKey = key,
            abfahrtHalt = abfahrt,
            ankunftHalt = ankunft,
            klasse,
        )
        return ReservierungsQuery(buchungskontext = ReservierungsQuery.Buchungskontext(buchungsKontextDaten = kontext))
    }

    fun fetchReservierung(query: ReservierungsQuery): ReservierungsDaten? {
        val connection = Jsoup.connect("https://www.bahn.de/web/api/gsd/gsd_v3/")
            .data("data", MAPPER.writeValueAsString(query))
            .referrer("https://www.bahn.de/buchung/fahrplan/angebotsauswahl")
            .method(Connection.Method.GET)
            .execute()

        val json: List<Any> = MAPPER.readValue(connection.parse().getElementById("__NUXT_DATA__")?.data() ?: return null)

        return parse(json)
    }
}

typealias Data = List<Any>
