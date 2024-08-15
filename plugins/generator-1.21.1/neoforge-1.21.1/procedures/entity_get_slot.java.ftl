<#include "mcitems.ftl">
/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid, Entity entity) {
		if (entity.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
			return _modHandler.getStackInSlot(sltid).copy();
		}
		return ItemStack.EMPTY;
	}
}.getItemStack(${opt.toInt(input$slotid)}, ${input$entity}))