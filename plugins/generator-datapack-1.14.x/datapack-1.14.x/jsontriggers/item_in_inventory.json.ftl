{
  "trigger": "minecraft:inventory_changed",
  "conditions": {
    "items": [
      {
        "item": "${input$item}",
        "count": {
          "min": ${input$amount_l},
          "max": ${input$amount_h}
        }
      }
    ]
  }
}