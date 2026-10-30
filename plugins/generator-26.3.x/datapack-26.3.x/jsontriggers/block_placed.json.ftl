"${registryname}_${cbi}": {
  "trigger": "minecraft:placed_block",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      ${input$player}
    },
    </#if>
    "location": {
      "type": "minecraft:match_block",
      "blocks": ${input$block}
    }
  }
},