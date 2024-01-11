package no.nav.tms.varsel.hendelse.gateway

import org.intellij.lang.annotations.Language
import java.time.ZonedDateTime

@Language("JSON")
fun varselOpprettetPacket(
    varseltype: String = "beskjed",
    varselId: String = "123",
    cluster: String? = "cluster",
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
    {
      "type": "$varseltype",
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
    varseltype: String = "beskjed",
    varselId: String = "123",
    cluster: String? = "cluster",
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "varselId": "$varselId",
  "varseltype": "$varseltype",
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

@Language("JSON")
internal fun varselArkivertPacket(
    varseltype: String = "beskjed",
    varselId: String = "123",
    cluster: String? = "cluster",
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "varselId": "$varselId",
  "varseltype": "$varseltype",
  "produsent": {
    "cluster": ${cluster?.let { "\"$it\"" } ?: "null"},
    "namespace": "$namespace",
    "appnavn": "$appnavn"
  },
  "opprettet": "${ZonedDateTime.now().minusYears(1)}",
  "tidspunkt": "${ZonedDateTime.now()}",
  "@event_name": "arkivert"
}
""".trimIndent()

@Language("JSON")
internal fun eksternStatusOppdatertPacket(
    status: String = "sendt",
    varselId: String = "123",
    ident: String = "12345678901",
    varseltype: String = "beskjed",
    kanal: String? = "SMS",
    renotifikasjon: Boolean? = false,
    feilmelding: String? = "Ekstern feil",
    cluster: String? = "cluster",
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "status": "$status",
  "varselId": "$varselId",
  "ident": "$ident",
  "varseltype": "$varseltype",
  "produsent": {
    "cluster": ${cluster?.let { "\"$it\"" } ?: "null"},
    "namespace": "$namespace",
    "appnavn": "$appnavn"
  },
  "renotifikasjon": ${renotifikasjon?.let { "$it" } ?: "null"},
  "feilmelding": ${feilmelding?.let { "\"$it\"" } ?: "null"},
  "kanal": ${kanal?.let { "\"$it\"" } ?: "null"},
  "tidspunkt": "${ZonedDateTime.now()}",
  "@event_name": "eksternStatusOppdatert"
}
""".trimIndent()
