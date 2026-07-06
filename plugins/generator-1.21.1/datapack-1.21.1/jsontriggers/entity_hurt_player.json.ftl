"${registryname}_${cbi}": {
  "trigger": "minecraft:entity_hurt_player",
  "conditions": {
    <#if input$player?has_content>
    "player": ${input$player},
    </#if>
    "damage": {
      "taken": {
        "min": ${input$minDamage},
        "max": ${input$maxDamage}
      },
      "source_entity": {
        "type": "${generator.map(field$sourceentity, "entities", 2)}"
      },
      "blocked": ${field$blocked}
    }
  }
},