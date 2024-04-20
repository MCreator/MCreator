<#include "mcitems.ftl">
/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid, ItemStack _isc) {
		IItemHandler _itemHandler = _isc.getCapability(Capabilities.ItemHandler.ITEM, null);
		if (_itemHandler != null)
			return _itemHandler.getStackInSlot(sltid).copy();
		return ItemStack.EMPTY;
	}
}.getItemStack(${opt.toInt(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)}))