Strukturen werden in einem Raster erzeugt. Diese beiden Parameter steuern das Raster:

* **Separation** - Mindestabstand in Chunks. Muss kleiner sein als Abstand.
* **Abstand** - Ungefähr die durchschnittliche Distanz zwischen zwei Strukturen in diesem Set.

Beispiel mit `Abstand = 5`, `Separation = 2`. Es wird in jedem 5x5 Chunk-Raster ein Strukturversuch geben, und nur bei `X` kann eine Struktur entstehen.

```
.............
..XXX..XXX..X
..XXX..X
..XXX..XXX..X
.................
.................
..XXX..XXX..X
..XXX..X
..XXX..X 
 ..XXX..XXX..X
```