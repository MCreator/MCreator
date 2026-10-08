This parameter controls spawning type for the biomes where this mob is defined to spawn in.

* A mob marked as Monster will only spawn in the dark or at night, and never in peaceful difficulty
* A mob marked as Creature will spawn in light level above 8 on blocks tagged with `minecraft:animals_spawnable_on` only. 
Do not use this spawn type with mob type living entities as they will not spawn.
* A mob marked as Ambient will spawn under any conditions except if block type prevents it,
but this category should be used for mobs with no gameplay effect such as bats
* WaterCreature will spawn in water that is at least two blocks deep, but with no other limitations
* WaterAmbient will spawn the same way as WaterCreature,
but this category should be used for mobs with no gameplay effect such as fish
* UndergroundWaterCreature will only spawn in water in complete darkness, at least 33 blocks below the sea level
* Axolotls uses the same spawning conditions as Monster, but mobs of this type are counted towards a separate mob cap
* A mob marked as Misc will not spawn naturally, as the game does not spawn mobs of this category

In Bedrock Edition add-ons, Monster will spawn in the dark, and WaterCreature will spawn underwater in oceans and rivers.
All other spawn types will spawn on grass blocks on the surface in light level 7 or above.

Spawn type system is in-depth explained [here](https://mcreator.net/wiki/mob-spawning-parameters)
