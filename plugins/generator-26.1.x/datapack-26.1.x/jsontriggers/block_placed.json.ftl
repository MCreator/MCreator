"${registryname}_${cbi}": {
  "trigger": "minecraft:placed_block",
  "conditions": {
    <#if input$player?has_content>
    "player": ${input$player},
    </#if>
    "location": [
      {
        "condition": "minecraft:block_state_property",
        "block": ${input$block}
      }
    ]
  }
},