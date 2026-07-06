"${registryname}_${cbi}": {
  "trigger": "minecraft:recipe_unlocked",
  "conditions": {
    <#if input$player?has_content>
    "player": ${input$player}
    </#if>
    "recipe": "${field$recipe}"
  }
},