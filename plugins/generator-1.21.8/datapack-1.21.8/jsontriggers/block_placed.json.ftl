"${registryname}_${cbi}": {
  "trigger": "minecraft:placed_block",
  "conditions": {
    "location": [
      {
        "condition": "minecraft:block_state_property",
        "block": "${input$block}"
      }
    ]
  }
},