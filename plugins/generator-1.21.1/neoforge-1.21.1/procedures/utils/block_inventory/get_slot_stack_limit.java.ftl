private static int getBlockInventorySlotStackLimit(LevelAccessor world, BlockPos pos, int slotId) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.getSlots())
			return itemHandler.getSlotLimit(slotId);
	}
	return 0;
}