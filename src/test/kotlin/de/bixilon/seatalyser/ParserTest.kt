package de.bixilon.seatalyser

import com.fasterxml.jackson.module.kotlin.readValue
import org.testng.annotations.Test
import kotlin.test.assertEquals

@Test(groups = ["parser"])
class ParserTest {


    private fun parse(name: String): ReservierungsDaten {
        val stream = ParserTest::class.java.getResourceAsStream("/$name.json")!!
        val data: List<Any> = MAPPER.readValue(stream)

        return ReserveriungsParser.parse(data)
    }

    fun `ice_803_class_2_wagen1`() {
        val data = parse("ice_803_class_2")

        assertEquals(data.carriage.size, 12)
        val wagon1 = data.carriage.find { it.number == 1 }!!
        assertEquals(wagon1.layout, "EPA_292")
        // assertEquals(wagon14.`class`, listOf(2))
        assertEquals(wagon1.seats.size, 51)
        assertEquals(wagon1.seats[118]?.status, ReservierungsDaten.Wagon.Seat.SeatStatus.RESERVED)
        assertEquals(wagon1.seats[117]?.status, ReservierungsDaten.Wagon.Seat.SeatStatus.AVAILABLE)
    }

    fun `ice_803_class_1wagen14`() {
        val data = parse("ice_803_class_1")

        assertEquals(data.carriage.size, 12)
        val wagon14 = data.carriage.find { it.number == 14 }!!
        assertEquals(wagon14.layout, "EPA_208")
        //   assertEquals(wagon14.`class`, listOf(1))
        assertEquals(wagon14.seats[104]?.status, ReservierungsDaten.Wagon.Seat.SeatStatus.AVAILABLE)
        val wagon1 = data.carriage.find { it.number == 1 }!!
        assertEquals(wagon1.seats, emptyMap()) // request is first class only
    }

    fun `ic_461_class_1`() {
        val data = parse("ic_461_class_1")

        val wagon4 = data.carriage.find { it.number == 4 }!!
        assertEquals(wagon4.seats[31]?.status, ReservierungsDaten.Wagon.Seat.SeatStatus.RESERVED)
        assertEquals(wagon4.seats[51]?.status, ReservierungsDaten.Wagon.Seat.SeatStatus.RESERVED)
        assertEquals(wagon4.seats[54]?.status, ReservierungsDaten.Wagon.Seat.SeatStatus.AVAILABLE)
    }
}
