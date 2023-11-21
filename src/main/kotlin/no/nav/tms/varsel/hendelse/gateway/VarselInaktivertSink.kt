package no.nav.tms.varsel.hendelse.gateway

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class VarselInaktivertSink(
    rapidsConnection: RapidsConnection,
    private val hendelseProducer: HendelseProducer
) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate { it.demandValue("@event_name", "inaktivert") }
            validate {
                it.requireKey("varselType")
                it.requireKey("varselId")
                it.requireKey("namespace")
                it.requireKey("appnavn")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val hendelse = VarselHendelse(
            hendelseType = packet["@event_name"].textValue(),
            varselType = packet["varselType"].textValue(),
            eventId = packet["varselId"].textValue(),
            cluster = null,
            namespace = packet["namespace"].textValue(),
            appnavn = packet["appnavn"].textValue()
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }
}

