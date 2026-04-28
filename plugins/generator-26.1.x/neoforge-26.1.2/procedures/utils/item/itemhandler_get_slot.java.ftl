private static ItemStack getItemStackFromItemStackSlot(int slotID, ItemStack itemStack) {
	ResourceHandler<ItemResource> itemHandler = itemStack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(itemStack));
	if (itemHandler != null)
		return itemHandler.getResource(slotID).toStack(itemHandler.getAmountAsInt(slotID));
	return ItemStack.EMPTY;
}