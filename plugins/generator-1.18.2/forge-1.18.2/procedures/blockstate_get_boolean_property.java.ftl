<#include "mcitems.ftl">
(${mappedBlockToBlock(input$block)}.getStateDefinition().getProperty(${input$property}) instanceof BooleanProperty _getbp && ${mappedBlockToBlockStateCode(input$block)}.getValue(_getbp))