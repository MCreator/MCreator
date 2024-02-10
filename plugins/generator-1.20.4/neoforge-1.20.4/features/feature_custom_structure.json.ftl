{
  "structure": "${modid}:${field$structure}",
  <#if field$random_rotation?lower_case == "true">"random_rotation": true,</#if>
  <#if field$random_mirror?lower_case == "true">"random_mirror": true,</#if>
  "ignored_blocks": ${input$ignored_blocks}
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    ${field$x},
    ${field$y},
    ${field$z}
  ]</#if>
}