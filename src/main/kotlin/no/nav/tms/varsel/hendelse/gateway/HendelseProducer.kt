package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.common.observability.traceVarsel
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class HendelseProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {
    private val log = KotlinLogging.logger {}

    private val objectMapper = defaultObjectMapper()

    fun sendVarselHendelse(hendelse: VarselHendelse) = traceVarsel(id = hendelse.varselId) {

        log.info { "Videresender ${hendelse.logDescription()}" }

        val hendelseJson = objectMapper.writeValueAsString(hendelse)

        val producerRecord = ProducerRecord(topicName, hendelse.varselId, hendelseJson)
        kafkaProducer.send(producerRecord)
    }
}

private fun defaultObjectMapper() = jacksonMapperBuilder()
    .addModule(JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .build()
    .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
