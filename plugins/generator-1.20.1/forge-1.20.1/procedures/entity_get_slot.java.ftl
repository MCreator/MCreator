<#include "mcitems.ftl">
/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid, Entity entity) {
		AtomicReference<ItemStack> _retval = new AtomicReference<>(ItemStack.EMPTY);
		entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
			_retval.set(capability.getStackInSlot(sltid).copy());
		});
		return _retval.get();
	}
}.getItemStack(${opt.toInt(input$slotid)}, ${input$entity}))