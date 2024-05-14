{
  "structure": "${modid}:${field$structure}",
  <#if field$random_rotation == "TRUE">"random_rotation": true,</#if>
  <#if field$random_mirror == "TRUE">"random_mirror": true,</#if>
  "ignored_blocks": ${input$ignored_blocks}
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    ${field$x},
    ${field$y},
    ${field$z}
  ]</#if>
}