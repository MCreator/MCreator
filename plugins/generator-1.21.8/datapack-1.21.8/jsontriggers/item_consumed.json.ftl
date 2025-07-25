"${registryname}_${cbi}": {
  "trigger": "minecraft:consume_item",
  "conditions": {
    "item": {
        "items": [
            "${input$item}"
        ]
    }
  }
},