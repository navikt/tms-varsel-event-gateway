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

internal class VarselArkivertSinkTest {

    private val hendelseTopic = "hendelseTopic"
    private val testRapid = TestRapid()

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
        VarselArkivertSink(testRapid, hendelseProducer)
    }

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
        val cluster = "produsent_cluster"

        val varselInaktivert = varselArkivertPacket(varselType, varselId, cluster, namespace, appnavn)

        testRapid.sendTestMessage(varselInaktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "slettet"
        hendelseJson["varseltype"].asText() shouldBe varselType
        hendelseJson["varselId"].asText() shouldBe varselId
        hendelseJson["cluster"].asText() shouldBe cluster
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `takler null i produsent-cluster`(varselType: String) {
        val varselId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val varselInaktivert = varselArkivertPacket(varselType, varselId, null, namespace, appnavn)

        testRapid.sendTestMessage(varselInaktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "slettet"
        hendelseJson["varseltype"].asText() shouldBe varselType
        hendelseJson["varselId"].asText() shouldBe varselId
        hendelseJson["cluster"] shouldBe null
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
    }

    private fun randomUUID() = UUID.randomUUID().toString()
}
