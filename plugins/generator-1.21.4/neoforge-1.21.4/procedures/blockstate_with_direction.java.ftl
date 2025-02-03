<#include "mcitems.ftl">
<@addTemplate file="utils/blockstate_props/with_direction.java.ftl"/>
/*@BlockState*/(blockStateWithDirection(${mappedBlockToBlockStateCode(input$block)}, ${input$value}))