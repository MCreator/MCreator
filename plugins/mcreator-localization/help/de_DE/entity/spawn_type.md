Dieser Parameter steuert den Erscheinungstyp für die Biome, in denen diese Kreatur zur Erscheinung definiert ist.

* Ein als Monster markierter Mob wird nur im Dunkeln oder in der Nacht spawnen und nie auf der Schwierigkeit "Friedlich"
* Eine als Creature markierte Kreatur spawnt nur ab Lichtstärke 8 auf Blöcken, die mit `minecraft:animals_spawnable_on` markiert sind. Benutze diesen Spawntyp nicht für Lebewesen des Typs Mob, da sie nicht spawnen werden.
* Ein als Ambient markierter Mob spawnt unter allen Bedingungen außer wenn der Blocktyp dies verhindert, aber diese Kategorie sollte für Mobs ohne Gameplay-Effekt wie Fledermäuse verwendet werden
* WaterCreature wird in Wasser spawnen, dass mind. zwei Blöcke tief ist, jedoch ohne andere Limitationen
* WaterAmbient spawnt genau wie WaterCreature, doch diese Kategorie sollte nur genutzt werden, wenn die Kreatur keine Auswirkung auf den Spielfluss hat (z.B. Fische)
* UndergroundWaterCreature wird nur in absoluter Dunkelheit im Wasser spawnen und mind. 33 Blöcke unter dem Meeresspiegel
* Axolotls verwendet die gleichen Spawnbedingungen wie Monster, aber Mobs dieser Art werden zu einem separaten Kreaturlimit gezählt
* Ein als Misc markierter Mob wird nicht natürlich spawnen, da das Spiel keine Monster dieser Kategorie spawnt

In den Add-ons der Bedrock Edition wird Monster im Dunkeln spawnen und WaterCreature wird in Ozeanen und Flüssen unter Wasser spawnen. Alle anderen Spawntypen spawnen auf Grasblöcken auf der Oberfläche ab Lichtstärke 7.

Spawn-Typsystem ist ausführlich erklärt [hier](https://mcreator.net/wiki/mob-spawning-parameters)
