package de.seatalyser

import de.seatalyser.scraper.reservation.ReservierungsParser
import org.testng.annotations.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@Test
class ReservierungsParserTest {

    fun buildEpa() {
        val id = 803
        val date = ZonedDateTime.of(2023, 10, 3, 0, 0, 0, 0, ReservierungsParser.TIMEZONE)

        val epa = ReservierungsParser.buildEpa(id, date)
        assertEquals("EPA#803_2023-10-03", epa)
    }

    fun abfahrtDate() {
        val date = ZonedDateTime.of(2023, 10, 5, 17, 48, 0, 0, ReservierungsParser.TIMEZONE)

        val string = ReservierungsParser.buildAbfahrt(date.toInstant())
        assertEquals("2023-10-05T17:48:00", string)
    }
}
