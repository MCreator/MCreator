private static ItemStack extractFromBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, boolean simulate) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.getSlots()) {
			return itemHandler.extractItem(slotId, amount, simulate);
		}
	}
	return ItemStack.EMPTY;
}