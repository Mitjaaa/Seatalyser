package de.bixilon.seatalyser

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Jsoup
import java.util.Date

fun main(args: Array<String>) {
    val query = ReservierungsQuery()
    val query2 = fetchReservierung("803", Date(2023, 10, 3), ReservierungsQuery.Buchungskontext.Kontext.Abfahrt("8010205", "2023-10-01T20:50:00"), 8010101, ReservierungsQuery.Buchungskontext.Kontext.KategorieCode.KLASSE_2)
    println(query2)
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

private fun Int.prependDate(): String {
    val string = toString()
    if (string.length == 2) return string
    return "0".repeat(2 - string.length) + string
}

fun fetchReservierung(zugnummer: String, datum: Date, abfahrt: ReservierungsQuery.Buchungskontext.Kontext.Abfahrt, ankunft: Int, klasse: ReservierungsQuery.Buchungskontext.Kontext.KategorieCode): List<Any>? {
    val key = "EPA#${zugnummer}_${datum.year}-${datum.month.prependDate()}-${datum.day.prependDate()}" // "EPA#803_2023-10-03
    val request = ReservierungsQuery(buchungskontext = ReservierungsQuery.Buchungskontext(buchungsKontextDaten = ReservierungsQuery.Buchungskontext.Kontext(
        zugnummer = zugnummer,
        zugfahrtKey = key,
        abfahrtHalt = abfahrt,
        ankunftHalt = ReservierungsQuery.Buchungskontext.Kontext.Ankunft(ankunft.toString()),
        klasse,
    )))

    return fetchReservierung(request)
}

fun fetchReservierung(query: ReservierungsQuery): List<Any>? {
    val connection = Jsoup.connect("https://www.bahn.de/web/api/gsd/gsd_v3/")
        .data("data", MAPPER.writeValueAsString(query))
        // .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        //.header("Cookies", """request_consent_v=3; AMCV_5FA50A5953FB37E50A4C98BC%40AdobeOrg=179643557%7CMCIDTS%7C19630%7CMCMID%7C15588490436155284577024708369218556623%7CMCAID%7CNONE%7CvVersion%7C5.5.0; s_cc=true; TS01309da6=0144e11a91353457b821bba7ef9a58065b8e92721cc66736386b7ce543c6e541bcf1e5534a996ad310be21713481b5ba1549b41b5c; utag_main=v_id:018ae229898b00230863e7e7bd8405065001405d00d7e${'$'}_sn:3${'$'}_se:3${'$'}_ss:0${'$'}_st:1696019717541${'$'}vapi_domain:bahn.de${'$'}ses_id:1696017913417%3Bexp-session${'$'}_pn:1%3Bexp-session""")
        .referrer("https://www.bahn.de/buchung/fahrplan/angebotsauswahl")
        //   .ignoreHttpErrors(true)
        .get()

    return MAPPER.readValue(connection.getElementById("__NUXT_DATA__")?.data() ?: return null)
}
