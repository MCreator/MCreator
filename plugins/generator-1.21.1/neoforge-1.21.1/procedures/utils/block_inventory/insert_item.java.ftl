private static int insertInBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, ItemStack itemstack, boolean simulate) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.getSlots()) {
			ItemStack inserted = itemstack.copy();
			inserted.setCount(amount);
			return itemHandler.insertItem(slotId, inserted, simulate).getCount();
		}
	}
	return itemstack.getCount();
}