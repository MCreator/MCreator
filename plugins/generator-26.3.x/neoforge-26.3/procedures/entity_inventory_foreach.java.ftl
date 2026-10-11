if (${input$entity}.getCapability(Capabilities.Item.ENTITY, null) instanceof ResourceHandler<ItemResource> _resourceHandlerIter) {
	for (int _idx = 0; _idx < _resourceHandlerIter.size(); _idx++) {
		ItemStack itemstackiterator = ItemUtil.getStack(_resourceHandlerIter, _idx);
		${statement$foreach}
	}
}