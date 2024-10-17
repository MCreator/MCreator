<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
	_setstack.setCount((int) ${input$amount});
	ItemHandlerHelper.giveItemToPlayer(((PlayerEntity)${input$entity}), _setstack);
}