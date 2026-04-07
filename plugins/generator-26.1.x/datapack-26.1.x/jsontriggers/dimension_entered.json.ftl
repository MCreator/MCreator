"${registryname}_${cbi}": {
  "trigger": "minecraft:changed_dimension",
  "conditions": {
    "to": "${generator.map(field$dimension, "dimensions", 1)}"
  }
},