if (${input$entity}.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
	for(int _idx = 0; _idx < _modHandler.getSlots(); _idx++) {
		ItemStack itemstackiterator = _modHandler.getStackInSlot(_idx).copy();
		${statement$foreach}
	}
}