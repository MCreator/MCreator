A struktúrák rács alapon generálódnak. Ez a két paraméter vezérli a rácsot:

* **separation** - A minimális távolság chunk-okban. Kisebbnek kell lennie, mint a spacing.
* **spacing** - Nagyjából az átlagos távolság chunk-okban két struktúra között ebben a készletben.

Example with `spacing = 5`, `separation = 2`. There will be one structure attempt in each 5x5 chunk grid, and only at `X` a structure can spawn.

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