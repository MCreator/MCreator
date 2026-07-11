<#include "mcitems.ftl">
<@addTemplate file="utils/blockstate_props/with_integer_property.java.ftl"/>
/*@BlockState*/(blockStateWithInt(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${opt.toInt(input$value)}))