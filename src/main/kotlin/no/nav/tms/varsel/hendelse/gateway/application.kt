package no.nav.tms.varsel.hendelse.gateway

import no.nav.tms.kafka.application.KafkaApplication
import no.nav.tms.kafka.producer.KafkaProducerBuilder

fun main() {
    val environment = Environment()

    val hendelseProducer = HendelseProducer(
        kafkaProducer = KafkaProducerBuilder.stringProducer(),
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

        minSideMdc {
            enabled = false
        }
    }.start()
}
