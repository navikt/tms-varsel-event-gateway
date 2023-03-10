package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class VarselSinkTest {

    private val hendelseTopic = "hendelseTopic"
    private val testRapid = TestRapid()
    private val testFnr = "12345678910"

    private val mockProducer = MockProducer(
        false,
        StringSerializer(),
        StringSerializer()
    )

    private val hendelseProducer = HendelseProducer(
        mockProducer,
        hendelseTopic
    )

    private val objectMapper = ObjectMapper()

    @BeforeAll
    fun setup() {
        VarselSink(testRapid, hendelseProducer)
    }

    @AfterEach
    fun cleanup() {
        mockProducer.clear()
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne aktivert-eventer og publiserer eksternt`(varselType: String) {
        val eventId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val varselAktivert = varselAktivertPacket(varselType, eventId, namespace, appnavn)

        testRapid.sendTestMessage(varselAktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].textValue() shouldBe "aktivert"
        hendelseJson["varselType"].textValue() shouldBe varselType
        hendelseJson["eventId"].textValue() shouldBe eventId
        hendelseJson["namespace"].textValue() shouldBe namespace
        hendelseJson["appnavn"].textValue() shouldBe appnavn
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne inaktivert-eventer og publiserer eksternt`(varselType: String) {
        val eventId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val varselAktivert = varselInaktivertPacket(varselType, eventId, namespace, appnavn)

        testRapid.sendTestMessage(varselAktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].textValue() shouldBe "inaktivert"
        hendelseJson["varselType"].textValue() shouldBe varselType
        hendelseJson["eventId"].textValue() shouldBe eventId
        hendelseJson["namespace"].textValue() shouldBe namespace
        hendelseJson["appnavn"].textValue() shouldBe appnavn
    }

    private fun randomUUID() = UUID.randomUUID().toString()
}
