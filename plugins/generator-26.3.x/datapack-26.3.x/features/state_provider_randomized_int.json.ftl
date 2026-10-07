<#include "mcitems_json.ftl">
/*@BlockStateProvider*/{
  "type": "minecraft:randomized_int",
  "source": ${mappedBlockToBlockStateProvider(input$source)},
  "property": "${field$property}",
  "values": ${input$value}
}