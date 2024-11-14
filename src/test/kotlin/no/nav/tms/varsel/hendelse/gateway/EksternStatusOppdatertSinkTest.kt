package no.nav.tms.varsel.hendelse.gateway

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import no.nav.tms.kafka.application.MessageBroadcaster
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
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

    private val filteredStatuses = listOf("bestilt", "sendt", "feilet", "venter", "kansellert")

    private val broadcaster = MessageBroadcaster(
        listOf(EksternStatusSubscriber(hendelseProducer, filteredStatuses))
    )

    private val objectMapper = ObjectMapper()

    @AfterEach
    fun cleanup() {
        mockProducer.clear()
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
        }

        mockProducer.findEvent { it["status"].asText() == "sendt" }.let {
            it["kanal"].asText() shouldBe "SMS"
            it["renotifikasjon"].asBoolean() shouldBe false
            it["sendtSomBatch"].asBoolean() shouldBe false
        }


        mockProducer.findEvent { it["status"].asText() == "feilet" }.let {
            it["feilmelding"].asText() shouldBe "Renotifikasjon feilet"
            it["renotifikasjon"].asBooleanOrNull().shouldBeNull()
            it["sendtSomBatch"].asBooleanOrNull().shouldBeNull()
        }
    }


    @ParameterizedTest
    @ValueSource(strings = ["beskjed", "oppgave", "innboks"])
    fun `tillater filtrering av statuser basert på config`(varseltype: String) {
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

        val sendtOnlySubscriber = EksternStatusSubscriber(hendelseProducer, listOf("sendt"))
        val sendtOnlyBroadcaster = MessageBroadcaster(listOf(sendtOnlySubscriber))


        sendtOnlyBroadcaster.broadcastJson(bestilt)
        sendtOnlyBroadcaster.broadcastJson(sendt)
        sendtOnlyBroadcaster.broadcastJson(ferdigstilt)
        sendtOnlyBroadcaster.broadcastJson(feilet)

        mockProducer.history().size shouldBe 1

        mockProducer.findEvent { it["status"].asText() == "sendt" }
            .let{
                it["@event_name"].asText() shouldBe "eksternStatusOppdatert"
                it["varseltype"].asText() shouldBe varseltype
                it["varselId"].asText() shouldBe varselId
                it["namespace"].asText() shouldBe namespace
                it["appnavn"].asText() shouldBe appnavn
                it["sendtSomBatch"].asBooleanOrNull().shouldNotBeNull()
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
