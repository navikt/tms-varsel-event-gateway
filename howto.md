# Lytte på varsel-hendelser

Min side publiserer en hendelse hver gang et varsel blir opprettet, inaktivert
eller slettet, og når status for ekstern varsling endrer seg. Lytt på dem hvis
teamet ditt trenger å følge livsløpet til varslene dere selv har sendt.

Topic: `min-side.aapen-varsel-hendelse-v1`

Tilgang: [aapen-varsel-hendelse-iac](https://github.com/navikt/min-side-brukervarsel-topic-iac/blob/main/dev-gcp/aapen-varsel-hendelse.yaml)

Kafka-nøkkel er `varselId`, slik at hendelser for ett og samme varsel beholder kronologien.

## Hendelsestyper

| `@event_name`            | beskrivelse                                                                  |
|--------------------------|------------------------------------------------------------------------------|
| `opprettet`              | Varselet er validert og opprettet, og er synlig for bruker.                  |
| `inaktivert`             | Varselet er inaktivert, for eksempel av produsent eller av bruker selv.      |
| `slettet`                | Varselet er slettet og er ikke lenger synlig for bruker eller saksbehandler. |
| `eksternStatusOppdatert` | Status for ekstern varsling på sms eller epost har endret seg.               |

## Felter

`opprettet`, `inaktivert` og `slettet` har feltene `varselId`, `varseltype`, `namespace`,
`appnavn` og `tidspunkt`. Tidspunkt er ISO-8601 med tidssone.

> [!NOTE]
> Disse tre hendelsene sendes også med `eventId` og `varselType`, som er duplikater av
> henholdsvis `varselId` og `varseltype`. Feltene er beholdt for konsumenter som ble skrevet
> før feltnavnene ble endret. Nye konsumenter bør bruke `varselId` og `varseltype`.

`eksternStatusOppdatert` har i tillegg `status`, og feltene `kanal`, `renotifikasjon`,
`sendtSomBatch`, `melding` og `feilmelding` avhengig av status. Felter uten verdi utelates
fra meldingen, og hendelsen har ikke `eventId`/`varselType`.

Mulige verdier for `status` er `venter`, `bestilt`, `sendt`, `feilet`, `kansellert` og
`ferdigstilt`.

## Eksempler

Oppgave med varselId "123" fra produsent "team-alfabet:abc" er opprettet.

```json
{
  "@event_name": "opprettet",
  "varselId": "123",
  "varseltype": "oppgave",
  "eventId": "123",
  "varselType": "oppgave",
  "namespace": "team-alfabet",
  "appnavn": "abc",
  "tidspunkt": "2026-01-15T09:00:00Z"
}
```

Beskjed med varselId "456", opprettet av app "team-alfabet:def", fjernet av bruker.

```json
{
  "@event_name": "inaktivert",
  "varselId": "456",
  "varseltype": "beskjed",
  "eventId": "456",
  "varselType": "beskjed",
  "namespace": "team-alfabet",
  "appnavn": "def",
  "tidspunkt": "2026-01-15T10:30:00Z"
}
```

Ekstern varsling for oppgaven over er sendt på sms.

```json
{
  "@event_name": "eksternStatusOppdatert",
  "status": "sendt",
  "varselId": "123",
  "varseltype": "oppgave",
  "kanal": "SMS",
  "renotifikasjon": false,
  "sendtSomBatch": false,
  "namespace": "team-alfabet",
  "appnavn": "abc",
  "tidspunkt": "2026-01-15T09:05:00Z"
}
```

## Kontakt

Ta kontakt med oss [på Slack](https://nav-it.slack.com/archives/C0912F59V29) hvis du lurer på noe.
