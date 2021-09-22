<#include "mcitems.ftl">
/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid, Entity entity) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
			_retval.set(capability.getStackInSlot(sltid).copy());
		});
		return _retval.get();
	}
}.getItemStack((int)(${input$slotid}), ${input$entity}))