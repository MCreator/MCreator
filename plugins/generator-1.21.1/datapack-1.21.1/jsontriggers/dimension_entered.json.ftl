"${registryname}_${cbi}": {
  "trigger": "minecraft:changed_dimension",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      ${input$player}
    },
    </#if>
    "to": "${generator.map(field$dimension, "dimensions", 1)}"
  }
},