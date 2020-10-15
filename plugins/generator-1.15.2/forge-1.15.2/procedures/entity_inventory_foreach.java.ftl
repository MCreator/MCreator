{
    ${input$entity}.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
		for(int _idx = 0; _idx < capability.getSlots(); _idx++) {
			ItemStack itemstackiterator = capability.getStackInSlot(_idx).copy();
            ${statement$foreach}
        }
	});
}