"${registryname}_${cbi}": {
  "trigger": "minecraft:recipe_crafted",
  "conditions": {
    "recipes": "${field$recipe}"
    <#if input$player?has_content>
    "player": {
      ${input$player}
    },
    </#if>
    "recipe_id": "${field$recipe}"
  }
},