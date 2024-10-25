package no.nav.tms.varsel.hendelse.gateway

import no.nav.tms.kafka.application.JsonMessage
import no.nav.tms.kafka.application.MessageException
import no.nav.tms.kafka.application.Subscriber
import no.nav.tms.kafka.application.Subscription
import java.time.ZonedDateTime

class VarselLifetimeEventSubscriber(
    private val hendelseProducer: HendelseProducer
) : Subscriber() {

    override fun subscribe() = Subscription.forEvents("opprettet", "inaktivert", "arkivert")
        .withFields(
            "varselId",
            "produsent",
            "tidspunkt"
        )
        .withOptionalFields("type", "varseltype")

    override suspend fun receive(jsonMessage: JsonMessage) {
        val hendelse = InternStatusHendelse(
            hendelseType = mapEventName(jsonMessage.eventName),
            varseltype = varselType(jsonMessage),
            varselId = jsonMessage["varselId"].asText(),
            namespace = jsonMessage["produsent"]["namespace"].asText(),
            appnavn = jsonMessage["produsent"]["appnavn"].asText(),
            tidspunkt = jsonMessage["tidspunkt"].asText().let { ZonedDateTime.parse(it) }
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }

    fun varselType(jsonMessage: JsonMessage): String {
        return jsonMessage.getOrNull("varseltype")?.asText()
            ?: jsonMessage.getOrNull("type")?.asText()
            ?: throw MessageException("Manglet felt for varseltype")
    }

    fun mapEventName(name: String) = when(name) {
        "arkivert" -> "slettet"
        else -> name
    }
}

