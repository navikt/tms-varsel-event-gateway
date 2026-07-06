package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import no.nav.tms.kafka.application.RetriableMessageException
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.errors.AuthenticationException
import org.apache.kafka.common.errors.OutOfOrderSequenceException
import org.apache.kafka.common.errors.ProducerFencedException

class HendelseProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {
    private val log = KotlinLogging.logger {}

    private val objectMapper = defaultObjectMapper()

    fun sendVarselHendelse(hendelse: VarselHendelse): Unit = withLoggingContext("minside_id" to hendelse.varselId) {

        log.info { "Videresender ${hendelse.logDescription()}" }

        val hendelseJson = objectMapper.writeValueAsString(hendelse)

        ProducerRecord(topicName, hendelse.varselId, hendelseJson)
            .let(::synchronizedSend)
    }

    private fun synchronizedSend(record: ProducerRecord<String, String>) {
        val result = try {
            kafkaProducer.send(record)
                .get()
        } catch (e: KafkaException) {
            when (e) {
                is AuthenticationException -> throw e
                else -> throw RecordSendException("Feil ved sending av varsel-event", e)
            }
        }

        if (!result.hasOffset()) {
            throw RecordSendException("Varsel-event ble ikke persistert på kafka")
        }
    }
}

class RecordSendException(msg: String, cause: Exception? = null): RetriableMessageException(msg, cause)

private fun defaultObjectMapper() = jacksonMapperBuilder()
    .addModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .build()
    .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
