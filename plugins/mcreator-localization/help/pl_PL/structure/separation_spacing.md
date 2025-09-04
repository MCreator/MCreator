Struktury są generowane w siatce. Te dwa parametry sterują siatką:

* **Separacja** - Minimalna odległość w fragmentach. Musi być mniejsza niż odstęp.
* **odstęp** - Średni dystans w chunkach między dwoma strukturami w tym zestawie.

Przykład z odstępem <0>= 5</0>, <0>separacja = 2</0>. W każdej sieci 5x5 chunk zostanie wykonana jedna próba strukturalna, i tylko w <0>X</0> struktura może się pojawić.

```
.............
..XXX..XXX..X
..XXX..X
..XXX..XXX..X
.............
.............
..XXX..XXX..X
..XXX..XXX..X
..XXX..XXX..XXX..X X X
```