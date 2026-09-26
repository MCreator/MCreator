private static int getBlockInventorySlotCount(LevelAccessor world, BlockPos pos) {
	if (world instanceof ILevelExtension ext) {
		ResourceHandler<ItemResource> itemHandler = ext.getCapability(Capabilities.Item.BLOCK, pos, null);
		if (itemHandler != null)
			return itemHandler.size();
	}
	return 0;
}