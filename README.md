# tms-varsel-event-gateway

App som videreformidler eventer fra internt brukervarsel-topic. 

Første omgang melder den når et varsel først aktiveres (tilgjengelig for bruker), eller når det inaktiveres (done, utløpt, fjernet av bruker)

## Eksempler

Oppgave med eventId "123" mottat fra produsent "abc" og lagret i database.

```json
{
  "@event_name": "aktivert",
  "varselType": "oppgave",
  "eventId": "123",
  "appnavn": "abc"
}
```

Beskjed med eventId "456", opprettet av app "def", fjernet av bruker.

```json
{
  "@event_name": "inaktivert",
  "varselType": "beskjed",
  "eventId": "456",
  "appnavn": "def"
}
```
