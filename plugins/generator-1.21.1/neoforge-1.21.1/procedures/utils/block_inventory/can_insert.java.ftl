public static boolean canInsertInBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, ItemStack itemstack) {
	if (world instanceof ILevelExtension ext) {
		IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.getSlots()) {
			itemstack.setCount(amount);
			return itemHandler.isItemValid(slotId, itemstack);
		}
	}
	return false;
}