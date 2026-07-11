<#include "mcitems.ftl">
<@addTemplate file="utils/blockstate_props/property_from_string.java.ftl"/>
(getPropertyByName(${mappedBlockToBlockStateCode(input$block)}, ${input$property}) instanceof BooleanProperty _getbp${cbi} && ${mappedBlockToBlockStateCode(input$block)}.getValue(_getbp${cbi}))