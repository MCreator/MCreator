<#include "mcitems.ftl">
if(entity instanceof EntityPlayer) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
	_setstack.setCount(${input$amount});
	ItemHandlerHelper.giveItemToPlayer(((EntityPlayer)entity), _setstack);
}