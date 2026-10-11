{
  "type": "minecraft:matching_blocks",
  "blocks": ${input$blockSet}
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    ${field$x},
    ${field$y},
    ${field$z}
  ]</#if>
}