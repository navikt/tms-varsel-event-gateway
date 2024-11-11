package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.tms.kafka.application.MessageBroadcaster
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class EksternStatusOppdatertSinkTest {

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

    private val broadcaster = MessageBroadcaster(
        listOf(EksternStatusSubscriber(hendelseProducer))
    )

    private val objectMapper = ObjectMapper()

    @BeforeAll
    fun setup() {
        EksternStatusSubscriber(hendelseProducer)
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

        val venter = eksternStatusOppdatertEvent(
            status = "venter",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val bestilt = eksternStatusOppdatertEvent(
            status = "bestilt",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val info = eksternStatusOppdatertEvent(
            status = "info",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val sendt = eksternStatusOppdatertEvent(
            status = "sendt",
            kanal = "SMS",
            varseltype = varseltype,
            renotifikasjon = false,
            batch = true,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val ferdigstilt = eksternStatusOppdatertEvent(
            status = "ferdigstilt",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val feilet = eksternStatusOppdatertEvent(
            status = "feilet",
            feilmelding = "Renotifikasjon feilet",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val kansellert = eksternStatusOppdatertEvent(
            status = "kansellert",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        broadcaster.broadcastJson(bestilt)
        broadcaster.broadcastJson(info)
        broadcaster.broadcastJson(sendt)
        broadcaster.broadcastJson(ferdigstilt)
        broadcaster.broadcastJson(feilet)
        broadcaster.broadcastJson(venter)
        broadcaster.broadcastJson(kansellert)

        mockProducer.history()
            .map{ objectMapper.readTree(it.value()) }
            .forEach{
                it["@event_name"].asText() shouldBe "eksternStatusOppdatert"
                it["varseltype"].asText() shouldBe varseltype
                it["varselId"].asText() shouldBe varselId
                it["namespace"].asText() shouldBe namespace
                it["appnavn"].asText() shouldBe appnavn
        }

        mockProducer.history()
            .map { objectMapper.readTree(it.value()) }
            .map { it["status"].asText() }
            .toList()
            .let {
                it shouldContain "bestilt"
                it shouldContain "sendt"
                it shouldContain "feilet"
                it shouldContain "venter"
                it shouldContain "kansellert"
                it shouldNotContain "info"
                it shouldNotContain "ferdigstilt"
            }

        mockProducer.findEvent { it["status"].asText() == "sendt" }.let {
            it["kanal"].asText() shouldBe "SMS"
            it["renotifikasjon"].asBoolean() shouldBe false
            it["sendtSomBatch"].asBoolean() shouldBe true
        }

        mockProducer.findEvent { it["status"].asText() == "feilet" }.let {
            it["feilmelding"].asText() shouldBe "Renotifikasjon feilet"
        }
    }


    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `plukker opp interne eksternVarslingStatusOppdatert-eventer og publiserer eksternt`(varseltype: String) {
        val varselId = randomUUID()
        val appnavn = "produsent_app"
        val namespace = "produsent_namespace"

        val bestilt = eksternVarslingStatusOppdatertEvent(
            status = "bestilt",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val sendt = eksternVarslingStatusOppdatertEvent(
            status = "sendt",
            kanal = "SMS",
            varseltype = varseltype,
            renotifikasjon = false,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val ferdigstilt = eksternVarslingStatusOppdatertEvent(
            status = "ferdigstilt",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn
        )

        val feilet = eksternVarslingStatusOppdatertEvent(
            status = "feilet",
            feilmelding = "Renotifikasjon feilet",
            varseltype = varseltype,
            varselId = varselId,
            namespace = namespace,
            appnavn = appnavn,
            batch = true
        )

        broadcaster.broadcastJson(bestilt)
        broadcaster.broadcastJson(sendt)
        broadcaster.broadcastJson(ferdigstilt)
        broadcaster.broadcastJson(feilet)

        mockProducer.history()
            .map{ objectMapper.readTree(it.value()) }
            .forEach{
                it["@event_name"].asText() shouldBe "eksternStatusOppdatert"
                it["varseltype"].asText() shouldBe varseltype
                it["varselId"].asText() shouldBe varselId
                it["namespace"].asText() shouldBe namespace
                it["appnavn"].asText() shouldBe appnavn
                it["sendtSomBatch"].asBooleanOrNull().shouldNotBeNull()
        }

        mockProducer.findEvent { it["status"].asText() == "sendt" }.let {
            it["kanal"].asText() shouldBe "SMS"
            it["renotifikasjon"].asBoolean() shouldBe false
            it["sendtSomBatch"].asBoolean() shouldBe false
        }


        mockProducer.findEvent { it["status"].asText() == "feilet" }.let {
            it["feilmelding"].asText() shouldBe "Renotifikasjon feilet"
            it["sendtSomBatch"].asBoolean() shouldBe true
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
