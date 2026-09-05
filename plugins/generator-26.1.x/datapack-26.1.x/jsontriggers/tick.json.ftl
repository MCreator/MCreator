"${registryname}_${cbi}": {
  "trigger": "minecraft:tick"
  <#if input$player?has_content>,
  "conditions": [
    "player": {
      ${input$player}
    }
  ]
  </#if>
},