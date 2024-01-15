package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.JsonNode
import no.nav.helse.rapids_rivers.*

class EksternStatusOppdatertSink(
    rapidsConnection: RapidsConnection,
    private val hendelseProducer: HendelseProducer
) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {  }
            validate {
                it.demandValue("@event_name", "eksternStatusOppdatert")
                it.demandAny("status", listOf("bestilt", "sendt", "feilet"))

                it.requireKey("varselId")
                it.requireKey("varseltype")
                it.requireKey("produsent")
                it.interestedIn("kanal")
                it.interestedIn("renotifikasjon")
                it.interestedIn("feilmelding")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val hendelse = EksternStatusHendelse(
            status = packet["status"].asText(),
            varselId = packet["varselId"].asText(),
            varseltype = packet["varseltype"].asText(),
            kanal = packet["kanal"].asTextOrNull(),
            renotifikasjon = packet["renotifikasjon"].asBooleanOrNull(),
            feilmelding = packet["feilmelding"].asTextOrNull(),
            namespace = packet["produsent"]["namespace"].asText(),
            appnavn = packet["produsent"]["appnavn"].asText(),
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }
}
