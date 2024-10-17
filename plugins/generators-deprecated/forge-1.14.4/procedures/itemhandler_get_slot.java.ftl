<#include "mcitems.ftl">
/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid, ItemStack _isc) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		_isc.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
			_retval.set(capability.getStackInSlot(sltid).copy());
		});
		return _retval.get();
	}
}.getItemStack((int)(${input$slotid}), ${mappedMCItemToItemStackCode(input$item, 1)}))