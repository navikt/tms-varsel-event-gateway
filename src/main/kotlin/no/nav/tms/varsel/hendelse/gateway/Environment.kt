package no.nav.tms.varsel.hendelse.gateway

import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar

data class Environment(
    val groupId: String = getEnvVar("GROUP_ID"),
    val kafkaBrokers: String = getEnvVar("KAFKA_BROKERS"),
    val kafkaTruststorePath: String = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
    val kafkaKeystorePath: String = getEnvVar("KAFKA_KEYSTORE_PATH"),
    val kafkaCredstorePassword: String = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
    val varselHendelseTopic: String = getEnvVar("VARSEL_HENDELSE_TOPIC")
    ) {
    val rapidConfig = mapOf(
        "KAFKA_BROKERS" to getEnvVar("KAFKA_BROKERS"),
        "KAFKA_CONSUMER_GROUP_ID" to getEnvVar("GROUP_ID"),
        "KAFKA_RAPID_TOPIC" to getEnvVar("RAPID_TOPIC"),
        "KAFKA_KEYSTORE_PATH" to kafkaKeystorePath,
        "KAFKA_CREDSTORE_PASSWORD" to kafkaCredstorePassword,
        "KAFKA_TRUSTSTORE_PATH" to kafkaTruststorePath,
        "KAFKA_RESET_POLICY" to "earliest",
        "HTTP_PORT" to "8080"
    )
}
