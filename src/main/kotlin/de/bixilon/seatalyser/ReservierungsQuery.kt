package de.bixilon.seatalyser

import com.fasterxml.jackson.annotation.JsonProperty
import de.bixilon.kutil.uuid.UUIDUtil.toUUID
import java.util.UUID

data class ReservierungsQuery(
    @JsonProperty("displayinformation") val displayInformation: DisplayInformation = DisplayInformation(),
    val buchungskontext: Buchungskontext = Buchungskontext(),
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
        val buchungsKontextDaten: Kontext = Kontext(),
    ) {

        data class Kontext(
            val zugnummer: String = "803",
            val zugfahrtKey: String = "EPA#803_2023-10-03",
            val abfahrtHalt: Abfahrt = Abfahrt("8010205", "2023-10-01T20:50:00"),
            val ankunftHalt: Ankunft = Ankunft("8010101"),
            val servicekategorieCode: KategorieCode = KategorieCode.KLASSE_2,
            val serviceKategorie: Kategorie = servicekategorieCode.kategorie,
            val anzahlReisende: Int = 1,
            val inventarsystem: String = "EPA",
            val inklusiveLastMinuteReservierung: Boolean = false,
            val kombinationsId: UUID = UUID.randomUUID(),
        ) {

            data class Abfahrt(
                val locationId: String,
                val abfahrtZeit: String,
            )

            data class Ankunft(
                val locationId: String,
                val ankunftZeit: String="0000-01-01T00:00:00",
            )

            enum class KategorieCode(val kategorie: Kategorie) {
                KLASSE_1(Kategorie.SITZPLATZ_KLASSE_1),
                KLASSE_2(Kategorie.SITZPLATZ_KLASSE_2),
                ;
            }

            enum class Kategorie {
                SITZPLATZ_KLASSE_1,
                SITZPLATZ_KLASSE_2,
                ;
            }
        }
    }

}
