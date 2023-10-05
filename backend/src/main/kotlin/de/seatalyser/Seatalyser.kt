package de.seatalyser

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.bixilon.kutil.cast.CastUtil.unsafeNull
import de.seatalyser.database.Database
import de.seatalyser.dto.Train
import de.seatalyser.dto.TrainRoute
import de.seatalyser.scraper.reservation.ReservierungsParser
import de.seatalyser.scraper.reservation.ReservierungsParser.fetchReservierung
import de.seatalyser.server.RestServer
import java.time.ZonedDateTime


object Seatalyser {
    val database: Database = unsafeNull()

    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting Seatalyser")

        println("Connecting to database...")
        // val database = Database.create()
        // database.connect()

        println("Connecting")

        println("Starting reservation fetcher...")

        RestServer().run()

        /*
        val train = Train(Train.TrainTypes.ICE, 603, emptyList())
        val route = TrainRoute(8010205, 8010101, ZonedDateTime.of(2023, 10, 5, 17, 48, 0, 0, ReservierungsParser.TIMEZONE).toInstant())
        val reservierung = fetchReservierung(ReservierungsParser.createQuery(train, route, 2))
        println(reservierung)
         */
    }
}


val MAPPER = JsonMapper.builder()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .disable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
    .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
    .build()
    .registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )
    .registerModule(JavaTimeModule())
    .setDefaultMergeable(true)
