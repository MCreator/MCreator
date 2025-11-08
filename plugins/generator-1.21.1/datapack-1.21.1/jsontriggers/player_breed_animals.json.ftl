"${registryname}_${cbi}": {
  "trigger": "minecraft:bred_animals",
  "conditions": {
    "parent": {
      "type": "${generator.map(field$entity, "entities", 2)}"
    },
    "partner": {
      "type": "${generator.map(field$entity, "entities", 2)}"
    }
  }
},