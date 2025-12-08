"${registryname}_${cbi}": {
  "trigger": "minecraft:item_durability_changed",
  "conditions": {
    "item": {
        ${input$item},
        "predicates": {
            "minecraft:damage": {
                "durability": {
                    "min": ${input$amount_l},
                    "max": ${input$amount_h}
                }
            }
        }
      }
  }
},