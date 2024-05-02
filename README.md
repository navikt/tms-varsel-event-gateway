# tms-varsel-event-gateway

App som videreformidler eventer fra internt brukervarsel-topic. 

Første omgang melder den når et varsel først aktiveres (tilgjengelig for bruker), eller når det inaktiveres (done, utløpt, fjernet av bruker)

## Eksempler

Oppgave med varselId "123" mottat fra produsent "team-alfabet:abc" og lagret i database.

```json
{
  "@event_name": "opprettet",
  "varseltype": "oppgave",
  "varselId": "123",
  "namespace": "team-alfabet",
  "appnavn": "abc"
}
```

Sms sendt for oppgave med varselId "123"

Beskjed med varselId "456", opprettet av app "team-alfabet:def", fjernet av bruker.

```json
{
  "@event_name": "inaktivert",
  "varseltype": "beskjed",
  "varselId": "456",
  "namespace": "team-alfabet",
  "appnavn": "def"
}
```

Beskjed med varselId "789", opprettet av app "team-alfabet:ghi", slettet etter 1 år.

```json
{
  "@event_name": "slettet",
  "varseltype": "beskjed",
  "varselId": "456",
  "namespace": "team-alfabet",
  "appnavn": "ghi"
}
```
