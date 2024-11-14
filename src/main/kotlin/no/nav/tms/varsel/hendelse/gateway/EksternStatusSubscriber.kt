package no.nav.tms.varsel.hendelse.gateway

import no.nav.tms.kafka.application.JsonMessage
import no.nav.tms.kafka.application.Subscriber
import no.nav.tms.kafka.application.Subscription
import java.time.ZonedDateTime

class EksternStatusSubscriber(
    private val hendelseProducer: HendelseProducer,
    private val eksternStatusFilter: List<String>
) : Subscriber() {

    override fun subscribe() = Subscription.forEvents("eksternVarslingStatusOppdatert")
        .withAnyValue("status", *eksternStatusFilter.toTypedArray())
        .withFields(
            "varselId",
            "varseltype",
            "produsent",
            "tidspunkt"
        )
        .withOptionalFields(
            "kanal",
            "renotifikasjon",
            "feilmelding",
            "batch"
        )

    override suspend fun receive(jsonMessage: JsonMessage) {
        val status = jsonMessage["status"].asText()

        val sendtSomBatch = if(status == "sendt") {
            jsonMessage.getOrNull("batch")?.asBoolean() ?: false
        } else {
            null
        }

        val hendelse = EksternStatusHendelse(
            status = status,
            varselId = jsonMessage["varselId"].asText(),
            varseltype = jsonMessage["varseltype"].asText(),
            kanal = jsonMessage.getOrNull("kanal")?.asText(),
            renotifikasjon = jsonMessage.getOrNull("renotifikasjon")?.asBoolean(),
            sendtSomBatch = sendtSomBatch,
            feilmelding = jsonMessage.getOrNull("feilmelding")?.asText(),
            namespace = jsonMessage["produsent"]["namespace"].asText(),
            appnavn = jsonMessage["produsent"]["appnavn"].asText(),
            tidspunkt = jsonMessage["tidspunkt"].asText().let { ZonedDateTime.parse(it) }
        )

        hendelseProducer.sendVarselHendelse(hendelse)

        PrometheusMetricsCollector.countVarselHendelse(hendelse)
    }
}
