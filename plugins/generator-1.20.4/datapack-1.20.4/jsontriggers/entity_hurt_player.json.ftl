"${registryname}_${cbi}": {
  "trigger": "minecraft:entity_hurt_player",
  "conditions": {
    "damage": {
      "taken": {
        "min": ${input$minDamage},
        "max": ${input$maxDamage}
      },
      "source_entity": {
        "type": "${field$source_entity}"
      },
      "blocked": ${field$blocked}
    }
  }
},