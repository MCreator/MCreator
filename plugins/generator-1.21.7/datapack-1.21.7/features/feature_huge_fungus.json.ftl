{
  "valid_base_block": ${input$ground},
  "stem_state": ${input$stem},
  "hat_state": ${input$hat},
  "decor_state": ${input$decor},
  "replaceable_blocks": ${input$replaceable_blocks}
  <#if field$planted == "TRUE">, "planted": "true"</#if>
}