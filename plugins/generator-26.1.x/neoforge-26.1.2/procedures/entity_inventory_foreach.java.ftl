{
	ResourceHandler<ItemResource> _resourceHandler = entity.getCapability(Capabilities.Item.ENTITY, null);
	if (_resourceHandler != null) {
		for(int _idx = 0; _idx < _resourceHandler.size(); _idx++) {
			ItemStack itemstackiterator = ItemUtil.getStack(_resourceHandler, _idx);
			${statement$foreach}
		}
	}
}