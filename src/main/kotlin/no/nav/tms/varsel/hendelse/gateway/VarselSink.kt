package no.nav.tms.varsel.hendelse.gateway

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class VarselSink(
    rapidsConnection: RapidsConnection,
    private val hendelseProducer: HendelseProducer
) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate { it.demandAny("@event_name", listOf("aktivert", "inaktivert")) }
            validate { it.rejectValue("@source", "varsel-authority") }
            validate {
                it.requireKey("varselType")
                it.requireKey("eventId")
                it.requireKey("namespace")
                it.requireKey("appnavn")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val hendelse = VarselHendelse(
            hendelseType = packet["@event_name"].textValue(),
            varselType = packet["varselType"].textValue().lowercase(),
            eventId = packet["eventId"].textValue(),
            namespace = packet["namespace"].textValue(),
            appnavn = packet["appnavn"].textValue(),
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }
}

