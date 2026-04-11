private static ItemStack getEntitySlot(Entity entity, int slot) {
	if (entity != null) {
		ResourceHandler<ItemResource> resourceHandler = entity.getCapability(Capabilities.Item.ENTITY, null);
		if (resourceHandler != null) {
			return ItemUtil.getStack(resourceHandler, slot);
		}
	}
	return ItemStack.EMPTY;
}