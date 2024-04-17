"${registryname}_${cbi}": {
  "trigger": "minecraft:entity_hurt_player",
  "conditions": {
    "damage": {
      "taken": {
        "min": ${input$minDamage},
        "max": ${input$maxDamage}
      },
      "source_entity": {
        "type": "${generator.map(field$source_entity, "entities", 2)}"
      },
      "blocked": ${field$blocked}
    }
  }
},