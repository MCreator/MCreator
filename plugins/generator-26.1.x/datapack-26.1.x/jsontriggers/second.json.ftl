"${registryname}_${cbi}": {
  "trigger": "minecraft:location",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      ${input$player}
    }
    </#if>
  }
},