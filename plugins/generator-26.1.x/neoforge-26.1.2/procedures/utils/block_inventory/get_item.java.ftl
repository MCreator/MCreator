private static ItemStack itemFromBlockInventory(LevelAccessor world, BlockPos pos, int slot) {
	if (world instanceof ILevelExtension ext) {
		ResourceHandler<ItemResource> itemHandler = ext.getCapability(Capabilities.Item.BLOCK, pos, null);
		if (itemHandler != null)
			return itemHandler.getResource(slot).toStack(itemHandler.getAmountAsInt(slot));
	}
	return ItemStack.EMPTY;
}