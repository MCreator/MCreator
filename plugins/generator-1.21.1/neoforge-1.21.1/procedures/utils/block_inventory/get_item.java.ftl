private static ItemStack itemFromBlockInventory(LevelAccessor world, BlockPos pos, int slot) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null)
			return itemHandler.getStackInSlot(slot).copy();
	}
	return ItemStack.EMPTY;
}