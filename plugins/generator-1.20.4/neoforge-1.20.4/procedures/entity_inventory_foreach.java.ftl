if (${input$entity}.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandlerForEach) {
	for(int _idx = 0; _idx < _modHandlerForEach.getSlots(); _idx++) {
		ItemStack itemstackiterator = _modHandlerForEach.getStackInSlot(_idx).copy();
		${statement$foreach}
	}
}