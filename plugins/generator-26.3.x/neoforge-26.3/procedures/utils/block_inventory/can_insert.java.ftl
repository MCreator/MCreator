public static boolean canInsertInBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, ItemStack itemstack) {
	if (world instanceof ILevelExtension ext) {
		ResourceHandler<ItemResource> itemHandler = ext.getCapability(Capabilities.Item.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.size()) {
			ItemStack inserted = itemstack.copy();
			inserted.setCount(amount);
			return ItemUtil.insertItemReturnRemaining(itemHandler, slotId, inserted, true, null).getCount() == 0;
		}
	}
	return false;
}