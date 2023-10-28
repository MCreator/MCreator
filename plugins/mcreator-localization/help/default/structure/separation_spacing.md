Structures are generated in a grid. Those two parameters control the grid:

* **separation** - The minimum distance in chunks. Needs to be smaller than spacing.
* **spacing** - Roughly the average distance in chunks between two structures in this set.

Example with `spacing = 5`, `separation = 2`. 
There will be one structure attempt in each 5x5 chunk grid, 
and only at `X` a structure can spawn.

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