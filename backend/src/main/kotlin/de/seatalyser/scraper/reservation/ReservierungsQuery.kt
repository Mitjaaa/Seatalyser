package de.seatalyser.scraper.reservation

import com.fasterxml.jackson.annotation.JsonProperty
import de.bixilon.kutil.exception.Broken
import java.util.*

data class ReservierungsQuery(
    @JsonProperty("displayinformation") val displayInformation: DisplayInformation = DisplayInformation(),
    val buchungskontext: Buchungskontext,
    @JsonProperty("correlationID") val correlationID: String = UUID.randomUUID().toString() + "_" + UUID.randomUUID().toString(),
    val lang: String = "de",
    val theme: String = "web",
) {

    data class DisplayInformation(
        val zugbezeichnung: String = "",
        val abfahrtsbahnhof: String = "",
        val ankunftsbahnhof: String = "",
    )

    data class Buchungskontext(
        val quellSystem: String = "SIMA",
        val buchungsKontextDaten: Kontext,
    ) {

        data class Kontext(
            val zugnummer: Int,
            val zugfahrtKey: String,
            val abfahrtHalt: Abfahrt,
            val ankunftHalt: Ankunft,
            val servicekategorieCode: KategorieCode,
            val serviceKategorie: Kategorie = servicekategorieCode.kategorie,
            val anzahlReisende: Int = 1,
            val inventarsystem: String = "EPA",
            val inklusiveLastMinuteReservierung: Boolean = false,
            val kombinationsId: UUID = UUID.randomUUID(),
        ) {

            data class Abfahrt(
                val locationId: Int,
                val abfahrtZeit: String,
            )

            data class Ankunft(
                val locationId: Int,
                val ankunftZeit: String = "0000-01-01T00:00:00",
            )

            enum class KategorieCode(val kategorie: Kategorie) {
                KLASSE_1(Kategorie.SITZPLATZ_KLASSE_1),
                KLASSE_2(Kategorie.SITZPLATZ_KLASSE_2),
                ;


                companion object {

                    fun byClass(`class`: Int) = when (`class`) {
                        1 -> KLASSE_1
                        2 -> KLASSE_2
                        else -> Broken("Not a class: $`class`")
                    }
                }
            }

            enum class Kategorie {
                SITZPLATZ_KLASSE_1,
                SITZPLATZ_KLASSE_2,
                ;
            }
        }
    }
}
