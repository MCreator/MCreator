"${registryname}_${cbi}": {
  "trigger": "minecraft:bred_animals",
  "conditions": {
    "parent": {
      "type": "${generator.map(field$parent, "entities", 2)}"
    },
    "partner": {
      "type": "${generator.map(field$partner, "entities", 2)}"
    }
  }
},