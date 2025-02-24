Le strutture sono generate in una griglia. Questi due parametri controllano codesta griglia:

* **separazione** - La distanza minima tra i chunk. Deve essere inferiore alla spaziatura.
* **spacing** - Circa la distanza media tra due strutture in questo set.

Esempio con spaziatura `= 5`, separazione `= 2`. Ci sarà un tentativo di struttura in ogni griglia 5x5 chunk, e solo a `X` una struttura può generarsi.

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