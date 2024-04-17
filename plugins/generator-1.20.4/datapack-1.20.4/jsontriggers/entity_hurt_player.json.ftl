"${registryname}_${cbi}": {
  "trigger": "minecraft:entity_hurt_player",
  "conditions": {
    "damage": {
      "taken": {
        "min": ${input$amount_l},
        "max": ${input$amount_h}
      },
      "source_entity": {
        "type": "${field$source_entity}",
      },
      "blocked": ${field$blocked},
    }
  }
},