package no.nav.tms.varsel.hendelse.gateway

import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidApplication.RapidApplicationConfig.Companion.fromEnv

fun main() {
    val environment = Environment()

    val hendelseProducer = HendelseProducer(
        kafkaProducer = initializeRapidKafkaProducer(environment),
        topicName = environment.varselHendelseTopic,
    )

    startRapid(
        environment = environment,
        hendelseProducer = hendelseProducer,
    )
}

private fun startRapid(
    environment: Environment,
    hendelseProducer: HendelseProducer
) {
    RapidApplication.Builder(fromEnv(environment.rapidConfig)).build().apply {
        VarselAktivertSink(
            rapidsConnection = this,
            hendelseProducer = hendelseProducer
        )
        VarselInaktivertSink(
            rapidsConnection = this,
            hendelseProducer = hendelseProducer
        )
    }.start()
}
