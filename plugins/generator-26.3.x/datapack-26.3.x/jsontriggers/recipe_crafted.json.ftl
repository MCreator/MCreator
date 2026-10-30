"${registryname}_${cbi}": {
  "trigger": "minecraft:recipe_crafted",
  "conditions": {
    <#if input$player?has_content>
    "player": {
      ${input$player}
    },
    </#if>
    "recipes": "${field$recipe}"
  }
},