package no.nav.tms.varsel.hendelse.gateway

import org.intellij.lang.annotations.Language

@Language("JSON")
fun varselAktivertPacket(
    varselType: String,
    eventId: String,
    namespace: String,
    appnavn: String
) = """
    {
      "@event_name": "aktivert",
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
    varselType: String,
    eventId: String,
    namespace: String,
    appnavn: String,
) = """
    {
      "@event_name": "inaktivert",
      "varselType": "$varselType",
      "eventId": "$eventId",
      "namespace": "$namespace",
      "appnavn": "$appnavn"
    }
""".trimIndent()
