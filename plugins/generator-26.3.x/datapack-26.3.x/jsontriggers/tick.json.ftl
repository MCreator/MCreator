"${registryname}_${cbi}": {
  "trigger": "minecraft:tick"
  <#if input$player?has_content>,
  "conditions": {
    "player": {
      "type": "minecraft:all_of",
      "terms": [
        ${input$player}
      ]
    }
  }
  </#if>
},