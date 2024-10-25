package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.JsonNode
import no.nav.tms.kafka.application.isMissingOrNull

fun JsonNode?.asTextOrNull() = if (this == null || isMissingOrNull()) null else asText()
fun JsonNode?.asBooleanOrNull() = if (this == null || isMissingOrNull()) null else asBoolean()
