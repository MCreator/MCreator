<#include "mcitems.ftl">
if(entity instanceof EntityPlayer) {
	((EntityPlayer)entity).inventory.armorInventory.set(${input$slotid}, ${mappedMCItemToItemStackCode(input$item, 1)});
	if(entity instanceof EntityPlayerMP)
		((EntityPlayerMP)entity).inventory.markDirty();
}

/*@ItemStack*/