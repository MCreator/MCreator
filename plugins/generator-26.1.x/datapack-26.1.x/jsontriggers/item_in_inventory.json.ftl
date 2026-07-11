"${registryname}_${cbi}": {
  "trigger": "minecraft:inventory_changed",
  "conditions": {
    "items": [
      {
        ${input$item}
      }
    ]
  }
},