<#include "mcitems.ftl">
/*@BlockState*/(${mappedBlockToBlock(input$block)}.getStateDefinition().getProperty(${input$property}) instanceof BooleanProperty _withbp ?
    ${mappedBlockToBlockStateCode(input$block)}.setValue(_withbp, ${input$value}) : ${mappedBlockToBlockStateCode(input$block)})