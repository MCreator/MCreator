<#include "mcitems.ftl">
/*@BlockStateProvider*/{
  "type": "minecraft:randomized_int_state_provider",
  "source": ${mappedBlockToBlockStateProvider(input$source)},
  "property": "${field$property}",
  "values": ${input$value}
}