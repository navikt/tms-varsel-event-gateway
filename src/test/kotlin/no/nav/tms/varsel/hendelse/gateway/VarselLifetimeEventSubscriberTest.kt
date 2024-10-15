package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import no.nav.tms.kafka.application.MessageBroadcaster
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class VarselLifetimeEventSubscriberTest {

    private val hendelseTopic = "hendelseTopic"

    private val mockProducer = MockProducer(
        false,
        StringSerializer(),
        StringSerializer()
    )

    private val hendelseProducer = HendelseProducer(
        mockProducer,
        hendelseTopic
    )

    private val broadcaster = MessageBroadcaster(listOf(VarselLifetimeEventSubscriber(hendelseProducer)))

    private val objectMapper = ObjectMapper()

    @AfterEach
    fun cleanup() {
        mockProducer.clear()
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne arkivert-eventer og publiserer eksternt`(varselType: String) {
        val varselId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val varselInaktivert = varselArkivertEvent(varselType, varselId, namespace, appnavn)

        broadcaster.broadcastJson(varselInaktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "slettet"
        hendelseJson["varseltype"].asText() shouldBe varselType
        hendelseJson["varselId"].asText() shouldBe varselId
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne inaktivert-eventer og publiserer eksternt`(varselType: String) {
        val eventId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val varselInaktivert = varselInaktivertEvent(varselType, eventId, namespace, appnavn)

        broadcaster.broadcastJson(varselInaktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "inaktivert"
        hendelseJson["varseltype"].asText() shouldBe varselType
        hendelseJson["varselType"].asText() shouldBe varselType
        hendelseJson["eventId"].asText() shouldBe eventId
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne aktivert-eventer og publiserer eksternt`(varseltype: String) {
        val varselId = randomUUID()
        val namespace = "produsent_namespace"
        val appnavn = "produsent_app"

        val varselAktivert = varselOpprettetEvent(
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        broadcaster.broadcastJson(varselAktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "opprettet"
        hendelseJson["varseltype"].asText() shouldBe varseltype
        hendelseJson["varselType"].asText() shouldBe varseltype
        hendelseJson["varselId"].asText() shouldBe varselId
        hendelseJson["eventId"].asText() shouldBe varselId
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
    }

    private fun randomUUID() = UUID.randomUUID().toString()
}
