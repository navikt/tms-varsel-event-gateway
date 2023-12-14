package no.nav.tms.varsel.hendelse.gateway

import org.intellij.lang.annotations.Language
import java.time.ZonedDateTime

@Language("JSON")
fun varselAktivertPacket(
    varselType: String = "beskjed",
    varselId: String = "123",
    namespace: String = "namespace",
    appnavn: String = "appnavn",
    cluster: String? = null
) = """
    {
      "type": "$varselType",
      "varselId": "$varselId",
      "ident": "0123456790",
      "sensitivitet": "substantial",
      "innhold": {
        "tekst": "Dette er en tekst",
        "link": "https://link",
        "tekster": []
      },
      "produsent": {
        "cluster": ${cluster?.let { "\"$it\"" } ?: "null" },
        "namespace": "$namespace",
        "appnavn": "$appnavn"
      },
      "eksternVarslingBestilling": {
        "prefererteKanaler": [
          "SMS"
        ],
        "smsVarslingstekst": null,
        "epostVarslingstittel": null,
        "epostVarslingstekst": null
      },
      "opprettet": "${ZonedDateTime.now()}",
      "aktivFremTil": null,
      "tidspunkt": "${ZonedDateTime.now()}",
      "@event_name": "opprettet"
    }
""".trimIndent()

@Language("JSON")
internal fun varselInaktivertPacket(
    varselType: String = "beskjed",
    varselId: String = "123",
    cluster: String? = null,
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "varselId": "$varselId",
  "varseltype": "$varselType",
  "produsent": {
    "cluster": ${cluster?.let { "\"$it\"" } ?: "null"},
    "namespace": "$namespace",
    "appnavn": "$appnavn"
  },
  "kilde": "produsent",
  "tidspunkt": "${ZonedDateTime.now()}",
  "@event_name": "inaktivert"
}
""".trimIndent()
