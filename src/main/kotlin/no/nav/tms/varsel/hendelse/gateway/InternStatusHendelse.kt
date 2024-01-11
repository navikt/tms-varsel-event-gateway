package no.nav.tms.varsel.hendelse.gateway

interface VarselHendelse {
    val varselId: String
    fun toJson(): String
}

data class InternStatusHendelse(
    override val varselId: String,
    val hendelseType: String,
    val varseltype: String,
    val cluster: String?,
    val namespace: String,
    val appnavn: String
) : VarselHendelse {
    override fun toJson() = """
        {
            "@event_name": "$hendelseType",
            "varseltype": "$varseltype",
            "varselType": "$varseltype",
            "eventId": "$varselId",
            "varselId": "$varselId",
            ${cluster?.let { "\"cluster\": \"$it\"," }?: "" }
            "namespace": "$namespace",
            "appnavn": "$appnavn"
        }
    """.trimIndent()
}

data class EksternStatusHendelse(
    override val varselId: String,
    val status: String,
    val varseltype: String,
    val kanal: String?,
    val renotifikasjon: Boolean?,
    val feilmelding: String?,
    val cluster: String?,
    val namespace: String,
    val appnavn: String
) : VarselHendelse {
    override fun toJson() = """
        {
            "@event_name": "eksternStatusOppdatert",
            "varseltype": "$varseltype",
            "varselId": "$varselId",
            "status": "$status",
            ${kanal?.let { "\"kanal\": \"$it\"," }?: "" }
            ${renotifikasjon?.let { "\"renotifikasjon\": $it," }?: "" }
            ${feilmelding?.let { "\"feilmelding\": \"$it\"," }?: "" }
            ${cluster?.let { "\"cluster\": \"$it\"," }?: "" }
            "namespace": "$namespace",
            "appnavn": "$appnavn"
        }
    """.trimIndent()
}
