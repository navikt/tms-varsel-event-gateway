package no.nav.tms.varsel.hendelse.gateway

import no.nav.tms.common.util.config.StringEnvVar.getEnvVar

data class Environment(
    val groupId: String = getEnvVar("GROUP_ID"),
    val kafkaBrokers: String = getEnvVar("KAFKA_BROKERS"),
    val kafkaTruststorePath: String = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
    val kafkaKeystorePath: String = getEnvVar("KAFKA_KEYSTORE_PATH"),
    val kafkaCredstorePassword: String = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
    val varselHendelseTopic: String = getEnvVar("VARSEL_HENDELSE_TOPIC"),
    val varselTopic: String = getEnvVar("VARSEL_TOPIC"),
)
