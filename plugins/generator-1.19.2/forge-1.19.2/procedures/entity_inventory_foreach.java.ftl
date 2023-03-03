{
	AtomicReference<IItemHandler> _ihr${customBlockIndex} = new AtomicReference<>();
	${input$entity}.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> _ihr${customBlockIndex}.set(capability));
	if (_ihr${customBlockIndex}.get() != null) {
		for(int _idx = 0; _idx < _ihr${customBlockIndex}.get().getSlots(); _idx++) {
			ItemStack itemstackiterator = _ihr${customBlockIndex}.get().getStackInSlot(_idx).copy();
			${statement$foreach}
		}
	}
}