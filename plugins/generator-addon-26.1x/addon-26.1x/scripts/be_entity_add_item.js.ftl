<#include "mcitems.ftl">
let _stack${cbi} = ${mappedMCItemToItemStackCode(input$item, 1)};
_stack${cbi}.amount = ${input$amount};
${input$entity}.getComponent("minecraft:inventory")?.container?.addItem(_stack${cbi});