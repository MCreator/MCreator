"${registryname}_${cbi}": {
  "trigger": "minecraft:placed_block",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      "type": "minecraft:all_of",
      "terms": [
        ${input$player}
      ]
    },
    </#if>
    "location": {
      "type": "minecraft:match_block",
      "blocks": ${input$block}
    }
  }
},