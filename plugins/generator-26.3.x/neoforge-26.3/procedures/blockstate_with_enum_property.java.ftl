<#include "mcitems.ftl">
<@addTemplate file="utils/blockstate_props/with_enum_property.java.ftl"/>
/*@BlockState*/(blockStateWithEnum(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))