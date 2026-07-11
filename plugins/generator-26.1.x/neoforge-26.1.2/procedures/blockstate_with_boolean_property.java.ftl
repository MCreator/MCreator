<#include "mcitems.ftl">
/*@BlockState*/(${mappedBlockToBlock(input$block)}.getStateDefinition().getProperty(${input$property}) instanceof BooleanProperty _withbp${cbi} ?
    ${mappedBlockToBlockStateCode(input$block)}.setValue(_withbp${cbi}, ${input$value}) : ${mappedBlockToBlockStateCode(input$block)})