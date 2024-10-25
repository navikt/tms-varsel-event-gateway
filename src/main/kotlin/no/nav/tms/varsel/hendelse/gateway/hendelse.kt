package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

interface VarselHendelse {
    val varselId: String
}

data class InternStatusHendelse(
    @JsonProperty("@event_name") val hendelseType: String,
    override val varselId: String,
    val varseltype: String,
    val namespace: String,
    val appnavn: String,
    val tidspunkt: ZonedDateTime
) : VarselHendelse {

    val eventId = varselId
    val varselType = varseltype
}

data class EksternStatusHendelse(
    override val varselId: String,
    val status: String,
    val varseltype: String,
    val kanal: String?,
    val renotifikasjon: Boolean?,
    val feilmelding: String?,
    val sendtSomBatch: Boolean?,
    val namespace: String,
    val appnavn: String,
    val tidspunkt: ZonedDateTime
) : VarselHendelse {

    @JsonProperty("@event_name") val eventName = "eksternStatusOppdatert"
}
