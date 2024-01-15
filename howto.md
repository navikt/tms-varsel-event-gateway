# Lytte på aktiverte og inaktiverte varsler

Topic: aapen-varsel-hendelse

Tilgang: [aapen-varsel-hendelse-iac](https://github.com/navikt/min-side-brukervarsel-topic-iac/blob/main/dev-gcp/aapen-varsel-hendelse.yaml)


## Eksempler

Oppgave med eventId "123" mottatt fra produsent "team-alfabet:abc" og lagret i database.

```json
{
  "@event_name": "aktivert",
  "varselType": "oppgave",
  "eventId": "123",
  "namespace": "team-alfabet",
  "appnavn": "abc"
}
```

Beskjed med eventId "456", opprettet av app "team-alfabet:def", fjernet av bruker.

```json
{
  "@event_name": "inaktivert",
  "varselType": "beskjed",
  "eventId": "456",
  "namespace": "team-alfabet",
  "appnavn": "def"
}
```
