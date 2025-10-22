Dieser Parameter steuert den unterirdischen Block unter der Ebene des Bodenblocks.

Normalerweise werden hier vanilla - oder benutzerdefinierte Erde für die meisten Biome verwendet.

Der Block sollte mit dem Block-Tag `minecraft:dirt` versehen werden, damit Pflanzen und Bäume normal auf der Oberflächen spawnen können.

Versuchen Sie komplexe Blöcke zu vermeiden, z.B.:

* Transparente Blöcke
* Blöcke, die keine vollen Würfel sind
* Blöcke mit "Tile entitiy", NBT-Tags oder eigenem Inventar
* Blöcke, die als Zielpunkt verwendet werden
* Blöcke, die ticken

Wenn Sie solche Blöcke verwenden, wird die Weltgeneration langsam sein und in der geladenen Welt kann es starke Verzögerungen geben.