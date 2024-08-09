"${registryname}_${cbi}": {
  "trigger": "minecraft:inventory_changed",
  "conditions": {
    "items": [
      {
        "items": [
            "${input$item}"
        ],
        "count": {
          "min": ${input$amount_l},
          "max": ${input$amount_h}
        }
      }
    ]
  }
},