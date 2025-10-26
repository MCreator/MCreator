"${registryname}_${cbi}": {
  "trigger": "minecraft:item_durability_changed",
  "conditions": {
    "item": {
        "items": [
            "${input$item}"
        ],
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