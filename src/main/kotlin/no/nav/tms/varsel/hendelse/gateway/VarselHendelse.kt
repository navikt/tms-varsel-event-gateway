package no.nav.tms.varsel.hendelse.gateway

data class VarselHendelse(
    val hendelseType: String,
    val varselType: String,
    val eventId: String,
    val namespace: String,
    val appnavn: String
) {
    fun toJson() = """
        {
            "@event_name": "$hendelseType",
            "varselType": "$varselType",
            "eventId": "$eventId",
            "namespace": "$namespace",
            "appnavn": "$appnavn"
        }
    """.trimIndent()
}

