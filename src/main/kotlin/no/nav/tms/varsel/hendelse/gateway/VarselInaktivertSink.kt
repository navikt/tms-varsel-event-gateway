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
                it.requireKey("varseltype")
                it.requireKey("varselId")
                it.requireKey("produsent")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val hendelse = VarselHendelse(
            hendelseType = packet["@event_name"].textValue(),
            varselType = packet["varseltype"].textValue(),
            eventId = packet["varselId"].textValue(),
            cluster = packet["produsent"]["cluster"]?.textValue(),
            namespace = packet["produsent"]["namespace"].textValue(),
            appnavn = packet["produsent"]["appnavn"].textValue()
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }
}

