"${registryname}_${cbi}": {
  "trigger": "minecraft:changed_dimension",
  "conditions": {
    "from": "${generator.map(field$dimension, "dimensions", 1)}"
  }
},