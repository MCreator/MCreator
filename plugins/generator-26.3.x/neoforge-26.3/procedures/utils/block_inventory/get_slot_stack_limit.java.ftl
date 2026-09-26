private static int getBlockInventorySlotStackLimit(LevelAccessor world, BlockPos pos, int slotId) {
	if (world instanceof ILevelExtension ext) {
		ResourceHandler<ItemResource> itemHandler = ext.getCapability(Capabilities.Item.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.size()) {
			return itemHandler.getCapacityAsInt(slotId, itemHandler.getResource(slotId));
		}
	}
	return 0;
}