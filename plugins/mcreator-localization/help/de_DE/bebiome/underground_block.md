Dieser Parameter steuert den Block, der unter der mittleren Blockebene generiert.
Die meisten Vanilla-Biome verwenden hierfür Stein.

Vermeide komplexe Blöcke wie:

- Transparente Blöcke
- Blöcke, die keine vollständigen Würfel sind
- Blöcke mit Kachel-Entität, NBT-Tags oder Inventar
- Blöcke, die als POI verwendet werden
- Blöcke, die ticken

Wenn du solche Blöcke verwendest, wird die Weltgenerierung langsam und die geladene Welt kann stark verzögert werden.