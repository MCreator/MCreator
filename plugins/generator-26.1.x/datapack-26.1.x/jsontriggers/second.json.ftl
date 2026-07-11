"${registryname}_${cbi}": {
  "trigger": "minecraft:location"
    <#if input$player?has_content>,
    "conditions": {
      "player": {
        ${input$player}
      }
    }
  </#if>
},