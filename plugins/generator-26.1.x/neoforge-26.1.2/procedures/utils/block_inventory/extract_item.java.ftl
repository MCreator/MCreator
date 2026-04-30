private static ItemStack extractFromBlockInventory(LevelAccessor world, BlockPos pos, int slotId, int amount, boolean simulate) {
	if (world instanceof ILevelExtension ext) {
		ResourceHandler<ItemResource> itemHandler = ext.getCapability(Capabilities.Item.BLOCK, pos, null);
		if (itemHandler != null && slotId >= 0 && slotId < itemHandler.size()) {
			ItemResource extractedResource = itemHandler.getResource(slotId);
			ItemStack extractedStack = ItemStack.EMPTY;
			if (extractedResource.isEmpty() || amount < 0)
				return extractedStack;
			try (var tx = Transaction.openRoot()) {
				int extracted = itemHandler.extract(slotId, extractedResource, amount, tx);
				extractedStack = extractedResource.toStack(extracted);
				if (!simulate)
					tx.commit();
			}
			return extractedStack;
		}
	}
	return ItemStack.EMPTY;
}