<#include "mcitems.ftl">
/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int _slotid, ItemStack _isc) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		_isc.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_capability -> {
			_retval.set(_capability.getStackInSlot(_slotid).copy());
		});
		return _retval.get();
	}
}.getItemStack(${opt.toInt(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)}))