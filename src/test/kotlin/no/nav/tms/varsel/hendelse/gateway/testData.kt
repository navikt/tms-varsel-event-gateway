package no.nav.tms.varsel.hendelse.gateway

import org.intellij.lang.annotations.Language
import java.time.ZonedDateTime

@Language("JSON")
fun varselOpprettetEvent(
    varseltype: String = "beskjed",
    varselId: String = "123",
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
        "cluster": "cluster",
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
internal fun varselInaktivertEvent(
    varseltype: String = "beskjed",
    varselId: String = "123",
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "varselId": "$varselId",
  "varseltype": "$varseltype",
  "produsent": {
    "cluster": "cluster",
    "namespace": "$namespace",
    "appnavn": "$appnavn"
  },
  "kilde": "produsent",
  "tidspunkt": "${ZonedDateTime.now()}",
  "@event_name": "inaktivert"
}
""".trimIndent()

@Language("JSON")
internal fun varselArkivertEvent(
    varseltype: String = "beskjed",
    varselId: String = "123",
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "varselId": "$varselId",
  "varseltype": "$varseltype",
  "produsent": {
    "cluster": "cluster",
    "namespace": "$namespace",
    "appnavn": "$appnavn"
  },
  "opprettet": "${ZonedDateTime.now().minusYears(1)}",
  "tidspunkt": "${ZonedDateTime.now()}",
  "@event_name": "arkivert"
}
""".trimIndent()


@Language("JSON")
internal fun eksternVarslingStatusOppdatertEvent(
    status: String = "sendt",
    varselId: String = "123",
    varseltype: String = "beskjed",
    kanal: String? = null,
    renotifikasjon: Boolean? = null,
    batch: Boolean? = null,
    feilmelding: String? = null,
    namespace: String = "namespace",
    appnavn: String = "appnavn"
) = """
{
  "status": "$status",
  "varselId": "$varselId",
  "varseltype": "$varseltype",
  "produsent": {
    "cluster": "cluster",
    "namespace": "$namespace",
    "appnavn": "$appnavn"
  },
  "renotifikasjon": ${renotifikasjon?.let { "$it" } ?: "null"},
  "batch": $batch,
  "feilmelding": ${feilmelding?.let { "\"$it\"" } ?: "null"},
  "kanal": ${kanal?.let { "\"$it\"" } ?: "null"},
  "tidspunkt": "${ZonedDateTime.now()}",
  "@event_name": "eksternVarslingStatusOppdatert"
}
""".trimIndent()
