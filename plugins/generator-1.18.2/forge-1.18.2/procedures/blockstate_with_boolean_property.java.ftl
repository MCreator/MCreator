<#include "mcitems.ftl">
/*@BlockState*/(${mappedBlockToBlock(input$block)}.getStateDefinition().getProperty(${input$property}) instanceof BooleanProperty _bp ?
    ${mappedBlockToBlockStateCode(input$block)}.setValue(_bp, ${input$value}) : ${mappedBlockToBlockStateCode(input$block)})