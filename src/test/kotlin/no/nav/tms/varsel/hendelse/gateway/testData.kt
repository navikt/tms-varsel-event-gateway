package no.nav.tms.varsel.hendelse.gateway

import org.intellij.lang.annotations.Language

@Language("JSON")
fun varselAktivertPacket(
    varselType: String = "beskjed",
    eventId: String = "123",
    namespace: String = "namespace",
    appnavn: String = "appnavn",
    source: String? = null
) = """
    {
      "@event_name": "aktivert",
      ${if (source != null) "\"@source\":\"$source\"," else ""}
      "varselType": "$varselType",
      "eventId": "$eventId",
      "appnavn": "$appnavn",
      "systembruker": "N/A",
      "namespace": "$namespace",
      "eventTidspunkt": "${LocalDateTimeHelper.nowAtUtc()}",
      "forstBehandlet": "${LocalDateTimeHelper.nowAtUtc()}",
      "fodselsnummer": "01234567890",
      "grupperingsId": "N/A",
      "tekst": "Tekst",
      "link": "http://link",
      "sikkerhetsnivaa": 3,
      "sistOppdatert": "${LocalDateTimeHelper.nowAtUtc()}",
      "aktiv": true,
      "eksternVarsling": true,
      "prefererteKanaler": [ "SMS", "EPOST" ],
      "smsVarslingstekst": null,
      "epostVarslingstekst": null,
      "epostVarslingstittel": null
    }
""".trimIndent()

@Language("JSON")
internal fun varselInaktivertPacket(
    varselType: String = "beskjed",
    eventId: String = "123",
    namespace: String = "namespace",
    appnavn: String = "appnavn",
    source: String? = null
) = """
    {
      "@event_name": "inaktivert",
      ${if (source != null) "\"@source\":\"$source\"," else ""}
      "varselType": "$varselType",
      "eventId": "$eventId",
      "namespace": "$namespace",
      "appnavn": "$appnavn"
    }
""".trimIndent()
