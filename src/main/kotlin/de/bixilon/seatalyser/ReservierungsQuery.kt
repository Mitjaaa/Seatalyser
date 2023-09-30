package de.bixilon.seatalyser

import com.fasterxml.jackson.annotation.JsonProperty
import de.bixilon.kutil.uuid.UUIDUtil.toUUID
import java.util.UUID

data class ReservierungsQuery(
    @JsonProperty("displayinformation") val displayInformation: DisplayInformation = DisplayInformation(),
    val buchungskontext: Buchungskontext = Buchungskontext(),
    @JsonProperty("correlationID") val correlationID: String = "afc23948-1614-4130-b1f7-71950535f628_4ec7f0d6-081c-40cf-a167-e16fe1a39cbc",
    val lang: String = "de",
    val theme: String = "web",
) {


    data class DisplayInformation(
        val zugbezeichnung: String = "ICE 803",
        val abfahrtsbahnhof: String = "Leipzig Hbf",
        val ankunftsbahnhof: String = "Erfurt Hbf",
    )

    data class Buchungskontext(
        val quellSystem: String = "SIMA",
        val buchungsKontextDaten: Kontext = Kontext(),
    ) {

        data class Kontext(
            val zugnummer: String = "803",
            val zugfahrtKey: String = "EPA#803_2023-10-01",
            val abfahrtHalt:Abfahrt = Abfahrt("8010205", "2023-10-01T20:50:00"),
            val ankunftHalt:Ankunft = Ankunft("8010101", "2023-10-01T21:29:00"),
            val servicekategorieCode:KategorieCode=KategorieCode.KLASSE_2,
            val serviceKategorie:Kategorie=Kategorie.SITZPLATZ_KLASSE_2,
            val anzahlReisende:Int=1,
            val inventarsystem:String="EPA",
            val inklusiveLastMinuteReservierung:Boolean=false,
            val kombinationsId:UUID ="15aa2664-96d1-448b-8d77-b96c38a80c42".toUUID(),
        ) {

            data class Abfahrt(
                val locationId: String,
                val abfahrtZeit: String,
            )

            data class Ankunft(
                val locationId: String,
                val ankunftZeit: String,
            )

            enum class KategorieCode {
                KLASSE_1,
                KLASSE_2,
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
