{
  "state": {
    <#if field$state?starts_with("CUSTOM:")>
      "Name": "${modid}:${field$state?ends_with(":Flowing")?then("flowing_","")}${generator.getRegistryNameFromFullName(field$state)}"
    <#else>
      "Name": "${generator.map(field$state, "fluids", 1)}"
    </#if>
  },
  "valid_blocks": ${input$valid_blocks}
  <#if field$requires_block_below != "TRUE">,
  "requires_block_below": false
  </#if>
  <#if field$rock_count != "4">,
  "rock_count": ${field$rock_count}
  </#if>
  <#if field$hole_count != "1">,
  "hole_count": ${field$hole_count}
  </#if>
}