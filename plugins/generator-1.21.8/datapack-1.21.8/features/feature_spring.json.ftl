{
  "state": {
    "Name": "${generator.map(field$state, "fluids", 1)}"
  },
  "valid_blocks": ${input$valid_blocks}
  <#if field$requires_block_below == "FALSE">,
  "requires_block_below": false
  </#if>
  <#if field$rock_count != "4">,
  "rock_count": ${field$rock_count}
  </#if>
  <#if field$hole_count != "1">,
  "hole_count": ${field$hole_count}
  </#if>
}