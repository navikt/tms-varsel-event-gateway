package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.isMissingOrNull

fun JsonNode.asTextOrNull() = if (isMissingOrNull()) null else asText()
fun JsonNode.asBooleanOrNull() = if (isMissingOrNull()) null else asBoolean()
