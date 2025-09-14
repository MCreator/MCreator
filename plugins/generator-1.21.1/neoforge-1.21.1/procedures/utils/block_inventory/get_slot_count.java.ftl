private static int getBlockInventorySlotCount(LevelAccessor world, BlockPos pos) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null)
			return itemHandler.getSlots();
	}
	return 0;
}