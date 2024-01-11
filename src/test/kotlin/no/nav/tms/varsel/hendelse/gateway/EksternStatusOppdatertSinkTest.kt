package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class EksternStatusOppdatertSinkTest {

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
        EksternStatusOppdatertSink(testRapid, hendelseProducer)
    }

    @AfterEach
    fun cleanup() {
        mockProducer.clear()
    }

    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne eksternStatusOppdatert-eventer og publiserer eksternt`(varseltype: String) {
        val varselId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val bestilt = eksternStatusOppdatertPacket(
            status = "bestilt",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val sendt = eksternStatusOppdatertPacket(
            status = "sendt",
            kanal = "SMS",
            varseltype = varseltype,
            renotifikasjon = false,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val ferdigstilt = eksternStatusOppdatertPacket(
            status = "ferdigstilt",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val feilet = eksternStatusOppdatertPacket(
            status = "feilet",
            feilmelding = "Renotifikasjon feilet",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        testRapid.sendTestMessage(bestilt)
        testRapid.sendTestMessage(sendt)
        testRapid.sendTestMessage(ferdigstilt)
        testRapid.sendTestMessage(feilet)

        mockProducer.history()
            .map{ objectMapper.readTree(it.value()) }
            .forEach{
                it["@event_name"].asText() shouldBe "eksternStatusOppdatert"
                it["varseltype"].asText() shouldBe varseltype
                it["varselId"].asText() shouldBe varselId
                it["namespace"].asText() shouldBe namespace
                it["appnavn"].asText() shouldBe appnavn
        }

        mockProducer.findEvent { it["status"].asText() == "sendt" }.let {
            it["kanal"].asText() shouldBe "SMS"
            it["renotifikasjon"].asBoolean() shouldBe false
        }


        mockProducer.findEvent { it["status"].asText() == "feilet" }.let {
            it["feilmelding"].asText() shouldBe "Renotifikasjon feilet"
        }


    }

    private fun MockProducer<String, String>.findEvent(predicate: (JsonNode) -> Boolean): JsonNode {
        return history()
            .map { jsonMapper().readTree(it.value()) }
            .firstOrNull(predicate)
            .shouldNotBeNull()
    }

    private fun randomUUID() = UUID.randomUUID().toString()
}
