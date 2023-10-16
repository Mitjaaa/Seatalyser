package de.seatalyser.server

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.util.JSONPObject
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.bixilon.kutil.json.JsonUtil.toJsonObject
import de.bixilon.kutil.primitive.IntUtil.toInt
import de.seatalyser.MAPPER
import de.seatalyser.dto.Train
import de.seatalyser.dto.TrainRoute
import de.seatalyser.scraper.reservation.ReservierungsParser
import de.seatalyser.scraper.reservation.ReservierungsParser.fetchReservierung
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.time.ZonedDateTime

class RestServer {

    fun run() {
        embeddedServer(Netty, 4848) {
            routing {
                get("/") {
                    call.respondText("Seatalyser is up and running!")
                }
                get("/reservations") {// ?train=int&start=int&end=int&class=int&date=string
                    val trainNumber = call.request.queryParameters["train"].toInt()
                    val trainStart = call.request.queryParameters["start"].toInt()
                    val trainEnd = call.request.queryParameters["end"].toInt()
                    val date = call.request.queryParameters["date"]!!.split("-") // year-month-day
                    val reservationClass = call.request.queryParameters["class"].toInt()

                    val train = Train(Train.TrainTypes.ICE, trainNumber, emptyList())
                    val route = TrainRoute(trainStart, trainEnd, ZonedDateTime.of(date.get(0).toInt(), date.get(1).toInt(), date.get(2).toInt(), 0, 0, 0, 0, ReservierungsParser.TIMEZONE).toInstant())
                    val reservierung = fetchReservierung(ReservierungsParser.createQuery(train, route, reservationClass))

                    call.respondText(MAPPER.writeValueAsString(reservierung))
                    /*
                    call.respondText(call.request.queryParameters["type"] as String
                            + "\n"
                            + call.request.queryParameters["start"] as String
                            + "\n"
                            + call.request.queryParameters["end"] as String
                            + "\n"
                            + call.request.queryParameters["class"] as String
                            + "\n"
                            + call.request.queryParameters["timestamp"] as String
                    )*/
                }
            }
        }.start(wait = true)
    }
}