package no.nav.tms.varsel.hendelse.gateway

import io.prometheus.client.Counter

object PrometheusMetricsCollector {

    private const val NAMESPACE = "varsel_event_gateway"

    private val VARSEL_AKTIVERT: Counter = Counter.build()
            .name("varsel_aktivert")
            .namespace(NAMESPACE)
            .help("Antall varsel-aktivert hendelser per type og produsent-app")
            .labelNames("varselType", "appnavn")
            .register()

    private val VARSEL_INAKTIVERT: Counter = Counter.build()
            .name("varsel_inaktivert")
            .namespace(NAMESPACE)
            .help("Antall varsel-inaktivert hendelser per type og produsent-app")
            .labelNames("varselType", "appnavn")
            .register()

    fun countVarselHendelse(hendelse: VarselHendelse) {
        when (hendelse) {
            is InternStatusHendelse -> when (hendelse.hendelseType) {
                "aktivert" -> VARSEL_AKTIVERT.labels(hendelse.varseltype, hendelse.appnavn).inc()
                "inaktivert" -> VARSEL_INAKTIVERT.labels(hendelse.varseltype, hendelse.appnavn).inc()
            }
            is EksternStatusHendelse -> {}
        }
    }
}
