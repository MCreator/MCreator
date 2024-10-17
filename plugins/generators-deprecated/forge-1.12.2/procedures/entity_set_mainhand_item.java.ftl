<#include "mcitems.ftl">
if(entity instanceof EntityLivingBase) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
	_setstack.setCount(${input$amount});
	((EntityLivingBase)entity).setHeldItem(EnumHand.MAIN_HAND, _setstack);
	if(entity instanceof EntityPlayerMP)
		((EntityPlayerMP)entity).inventory.markDirty();
}