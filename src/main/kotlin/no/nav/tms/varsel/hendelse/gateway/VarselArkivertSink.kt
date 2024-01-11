package no.nav.tms.varsel.hendelse.gateway

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class VarselArkivertSink(
    rapidsConnection: RapidsConnection,
    private val hendelseProducer: HendelseProducer
) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate { it.demandValue("@event_name", "arkivert") }
            validate {
                it.requireKey("varselId")
                it.requireKey("varseltype")
                it.requireKey("produsent")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val hendelse = InternStatusHendelse(
            hendelseType = "slettet",
            varseltype = packet["varseltype"].asText(),
            varselId = packet["varselId"].asText(),
            cluster = packet["produsent"]["cluster"].asTextOrNull(),
            namespace = packet["produsent"]["namespace"].asText(),
            appnavn = packet["produsent"]["appnavn"].asText(),
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }
}

