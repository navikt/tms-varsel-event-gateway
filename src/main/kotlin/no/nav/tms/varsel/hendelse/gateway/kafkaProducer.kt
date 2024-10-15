package no.nav.tms.varsel.hendelse.gateway

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*


fun initializeKafkaProducer(environment: Environment) = KafkaProducer<String, String>(
    Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.kafkaBrokers)
        put(
            ProducerConfig.CLIENT_ID_CONFIG,
            "tms-varsel-event-gateway"
        )
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 40000)
        put(ProducerConfig.ACKS_CONFIG, "all")
        put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
        put(SaslConfigs.SASL_MECHANISM, "PLAIN")
        put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL")
        put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "jks")
        put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12")
        put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, environment.kafkaTruststorePath)
        put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, environment.kafkaCredstorePassword)
        put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, environment.kafkaKeystorePath)
        put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, environment.kafkaCredstorePassword)
        put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, environment.kafkaCredstorePassword)
        put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "")
    }
)
