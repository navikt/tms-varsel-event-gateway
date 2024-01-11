package no.nav.tms.varsel.hendelse.gateway

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class HendelseProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {
    fun sendVarselHendelse(hendelse: VarselHendelse) {

        val hendelseJson = hendelse.toJson()

        val producerRecord = ProducerRecord(topicName, hendelse.varselId, hendelseJson)
        kafkaProducer.send(producerRecord)
    }
}
