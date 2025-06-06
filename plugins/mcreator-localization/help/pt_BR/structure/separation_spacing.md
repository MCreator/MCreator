As estruturas são geradas em uma grade. Esses dois parâmetros controlam a grade:

* **separação** - A distância mínima em blocos. Precisa ser menor que o espaçamento.
* **espaçamento** - Aproximadamente a distância média em pedaços entre duas estruturas neste conjunto.

Exemplo com `espaçamento = 5`, `separação = 2`. Haverá uma tentativa de estrutura em cada grade de blocos 5x5, e somente em `X` uma estrutura poderá ser gerada.

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