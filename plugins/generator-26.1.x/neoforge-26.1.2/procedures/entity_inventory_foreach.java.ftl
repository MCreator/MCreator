if (entity.getCapability(Capabilities.Item.ENTITY, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	for (int _idx = 0; _idx < _resourceHandler.size(); _idx++) {
		ItemStack itemstackiterator = ItemUtil.getStack(_resourceHandler, _idx);
		${statement$foreach}
	}
}