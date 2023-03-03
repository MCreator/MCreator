{
	Optional<IItemHandler> _ihr${customBlockIndex} = ${input$entity}.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
	if (_ihr${customBlockIndex}.isPresent()) {
		for(int _idx${customBlockIndex} = 0; _idx${customBlockIndex} < _ihr${customBlockIndex}.get().getSlots(); _idx${customBlockIndex}++) {
			ItemStack itemstackiterator = _ihr${customBlockIndex}.get().getStackInSlot(_idx${customBlockIndex}).copy();
			${statement$foreach}
		}
	}
}