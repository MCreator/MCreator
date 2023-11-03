Les structures sont générées dans une grille. Ces deux paramètres contrôlent la grille :

* **separation** - La distance minimale en chunks. Doit être plus petite que l’espacement.
* **spacing** - Approximativement la distance moyenne en chunks entre deux structures dans cet ensemble.

Exemple avec `spacing = 5`, `separation = 2`. Il y aura une tentative de structure dans chaque grille de 5x5 chunks, et seulement à `X` une structure peut apparaître.

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