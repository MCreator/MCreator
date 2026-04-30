private static int insertInBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, ItemStack itemstack, boolean simulate) {
	if (world instanceof ILevelExtension ext) {
		ResourceHandler<ItemResource> itemHandler = ext.getCapability(Capabilities.Item.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.size()) {
			itemstack.setCount(amount);
			return ItemUtil.insertItemReturnRemaining(itemHandler, slotId, itemstack, simulate, null).getCount();
		}
	}
	return itemstack.getCount();
}