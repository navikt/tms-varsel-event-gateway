package no.nav.tms.varsel.hendelse.gateway

import io.prometheus.metrics.core.metrics.Counter

object PrometheusMetricsCollector {

    private const val NAMESPACE = "varsel_event_gateway"

    private val VARSEL_AKTIVERT: Counter = Counter.builder()
            .name("${NAMESPACE}_varsel_aktivert")
            .help("Antall varsel-aktivert hendelser per type og produsent-app")
            .labelNames("varselType", "appnavn")
            .register()

    private val VARSEL_INAKTIVERT: Counter = Counter.builder()
            .name("${NAMESPACE}_varsel_inaktivert")
            .help("Antall varsel-inaktivert hendelser per type og produsent-app")
            .labelNames("varselType", "appnavn")
            .register()

    fun countVarselHendelse(hendelse: VarselHendelse) {
        when (hendelse) {
            is InternStatusHendelse -> when (hendelse.hendelseType) {
                "aktivert" -> VARSEL_AKTIVERT.labelValues(hendelse.varseltype, hendelse.appnavn).inc()
                "inaktivert" -> VARSEL_INAKTIVERT.labelValues(hendelse.varseltype, hendelse.appnavn).inc()
            }
            is EksternStatusHendelse -> {}
        }
    }
}
