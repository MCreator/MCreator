"${registryname}_${cbi}": {
  "trigger": "minecraft:location",
  "conditions": {
    "player": [
        {
            "condition": "minecraft:entity_properties",
            "entity": "this",
            "predicate": {
              "location": {
                "biome": "${generator.map(field$biome, "biomes")}"
              }
            }
        }
    ]
  }
},