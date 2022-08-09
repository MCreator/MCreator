<#include "mcitems.ftl">
/*@BlockState*/(${mappedBlockToBlock(input$block)}.getStateDefinition().getProperty(${input$property}) instanceof BooleanProperty _withbp${customBlockIndex} ?
    ${mappedBlockToBlockStateCode(input$block)}.setValue(_withbp${customBlockIndex}, ${input$value}) : ${mappedBlockToBlockStateCode(input$block)})