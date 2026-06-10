"${registryname}_${cbi}": {
  "trigger": "minecraft:item_durability_changed",
  "conditions": {
    "item": {
        ${input$item}
      }
  }
},