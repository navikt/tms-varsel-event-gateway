package no.nav.tms.varsel.hendelse.gateway

import no.nav.tms.kafka.application.KafkaApplication

fun main() {
    val environment = Environment()

    val hendelseProducer = HendelseProducer(
        kafkaProducer = initializeKafkaProducer(environment),
        topicName = environment.varselHendelseTopic,
    )

    KafkaApplication.build {
        kafkaConfig {
            groupId = environment.groupId
            readTopic(environment.varselTopic)
        }
        subscribers(
            VarselLifetimeEventSubscriber(hendelseProducer),
            EksternStatusSubscriber(hendelseProducer, environment.eksternStatusFilter)
        )
    }.start()
}
