private static int insertInBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, ItemStack itemstack, boolean simulate) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.getSlots()) {
			itemstack.setCount(amount);
			return itemHandler.insertItem(slotId, itemstack, simulate).getCount();
		}
	}
	return itemstack.getCount();
}