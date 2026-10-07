"${registryname}_${cbi}": {
  "trigger": "minecraft:placed_block",
  "conditions": {
    "location": {
      "type": "minecraft:match_block",
      "blocks": ${input$block}
    }
  }
},