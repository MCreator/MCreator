"${registryname}_${cbi}": {
  "trigger": "minecraft:player_killed_entity",
  "conditions": {
      "entity": {
        "type": "${generator.map(field$entity, "entities", 2)}"
      }
    }
},