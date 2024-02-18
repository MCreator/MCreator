Strukturer genereres i et rutenett. De to parametrene styrer rutenettet:

* **separation** – Minste avstand i chunks. Må være mindre enn avstand.
* **spacing** – Omtrent gjennomsnittlig avstand i chunks mellom to strukturer i dette settet.

Eksempel med `spacing = 5`, `separation = 2`. Det vil være ett strukturforsøk i hvert 5x5 sentrum, og bare ved `X` kan en struktur spawne.

```
.............
..XXX..XXX..X
..XXX..XXX..X
..XXX..XXX..X
.............
.............
..XXX..XXX..X
..XXX..XXX..X
..XXX..XXX..X
```