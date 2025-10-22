if (${input$entity}.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandlerIter) {
	for(int _idx = 0; _idx < _modHandlerIter.getSlots(); _idx++) {
		ItemStack itemstackiterator = _modHandlerIter.getStackInSlot(_idx).copy();
		${statement$foreach}
	}
}