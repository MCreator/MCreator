<#include "mcitems.ftl">
<@addTemplate file="utils/blockstate_props/property_from_string.java.ftl"/>
/*@int*/(getPropertyByName(${mappedBlockToBlockStateCode(input$block)}, ${input$property}) instanceof IntegerProperty _getip${cbi} ? ${mappedBlockToBlockStateCode(input$block)}.getValue(_getip${cbi}) : -1)