<#include "mcitems.ftl">
{
  "blocks": {
    "filling_provider": ${mappedBlockToBlockStateProvider(input$filling)},
    "inner_layer_provider": ${mappedBlockToBlockStateProvider(input$inner_layer)},
    "alternate_inner_layer_provider": ${mappedBlockToBlockStateProvider(input$alternate_inner_layer)},
    "middle_layer_provider": ${mappedBlockToBlockStateProvider(input$middle_layer)},
    "outer_layer_provider": ${mappedBlockToBlockStateProvider(input$outer_layer)},
    "inner_placements": [
      <#list input_list$crystal as crystal>${crystal}<#sep>,</#list>
    ],
    "cannot_replace": "#${field$cannot_replace_tag}",
    "invalid_blocks": "#${field$invalid_blocks_tag}"
  },
  "layers": {
    <#if field$size != "1">
    <#assign scale = field$size?number>
    "filling": ${1.7 * scale},
    "inner_layer": ${2.2 * scale},
    "middle_layer": ${3.2 * scale},
    "outer_layer": ${4.2 * scale}
    </#if>
  },
  "crack": {},
  "invalid_blocks_threshold": ${field$invalid_blocks_count},
  "use_alternate_layer0_chance": 0.083
}