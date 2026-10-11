"${registryname}_${cbi}": {
  "trigger": "minecraft:entity_hurt_player",
  "conditions": {
    "damage": {
      "taken": {
        "min": ${input$minDamage},
        "max": ${input$maxDamage}
      },
      "source_entity": {
        "minecraft:entity_type": "${generator.map(field$sourceentity, "entities", 2)}"
      },
      "blocked": ${field$blocked}
    }
  }
},