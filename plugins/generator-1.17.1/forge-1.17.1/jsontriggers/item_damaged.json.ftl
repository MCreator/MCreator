{
  "trigger": "minecraft:item_durability_changed",
  "conditions": {
    "items": [
      {
        "items": [
            "${input$item}"
        ],
        "durability": {
          "min": ${input$amount_l},
          "max": ${input$amount_h}
        }
      }
    ]
  }
}