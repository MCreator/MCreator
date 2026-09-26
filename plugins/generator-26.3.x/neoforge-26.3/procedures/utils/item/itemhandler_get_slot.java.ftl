private static ItemStack getItemStackFromItemStackSlot(int slotID, ItemStack itemStack) {
	ResourceHandler<ItemResource> itemHandler = itemStack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(itemStack));
	if (itemHandler != null)
		return ItemUtil.getStack(itemHandler, slotID);
	return ItemStack.EMPTY;
}