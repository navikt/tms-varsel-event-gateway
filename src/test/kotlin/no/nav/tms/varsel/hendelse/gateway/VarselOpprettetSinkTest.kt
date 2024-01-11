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

internal class VarselOpprettetSinkTest {

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
        VarselOpprettetSink(testRapid, hendelseProducer)
    }

    @AfterEach
    fun cleanup() {
        mockProducer.clear()
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne aktivert-eventer og publiserer eksternt`(varseltype: String) {
        val varselId = randomUUID()
        val cluster = "produsent_cluster"
        val namespace = "produsent_namespace"
        val appnavn = "produsent_app"

        val varselAktivert = varselOpprettetPacket(
            varseltype = varseltype,
            varselId = varselId,
            cluster = cluster,
            namespace = namespace,
            appnavn = appnavn
        )

        testRapid.sendTestMessage(varselAktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "opprettet"
        hendelseJson["varseltype"].asText() shouldBe varseltype
        hendelseJson["varselType"].asText() shouldBe varseltype
        hendelseJson["varselId"].asText() shouldBe varselId
        hendelseJson["eventId"].asText() shouldBe varselId
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
        hendelseJson["cluster"].asText() shouldBe cluster
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `takler at produsent-cluster er null`(varselType: String) {
        val varselId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"
        val cluster = null

        val varselAktivert = varselOpprettetPacket(
            varseltype = varselType,
            varselId = varselId,
            cluster = cluster,
            namespace = namespace,
            appnavn = appnavn
        )

        testRapid.sendTestMessage(varselAktivert)

        val hendelse = mockProducer.history().first().value()

        val hendelseJson = objectMapper.readTree(hendelse)

        hendelseJson["@event_name"].asText() shouldBe "opprettet"
        hendelseJson["varseltype"].asText() shouldBe varselType
        hendelseJson["varselId"].asText() shouldBe varselId
        hendelseJson["eventId"].asText() shouldBe varselId
        hendelseJson["namespace"].asText() shouldBe namespace
        hendelseJson["appnavn"].asText() shouldBe appnavn
        hendelseJson["cluster"] shouldBe cluster
    }

    private fun randomUUID() = UUID.randomUUID().toString()
}
