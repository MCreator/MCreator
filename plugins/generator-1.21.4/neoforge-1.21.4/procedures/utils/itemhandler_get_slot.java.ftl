private static ItemStack getItemStackFromItemStackSlot(int slotID, ItemStack itemStack) {
	IItemHandler itemHandler = itemStack.getCapability(Capabilities.ItemHandler.ITEM, null);
	if (itemHandler != null)
		return itemHandler.getStackInSlot(slotID).copy();
	return ItemStack.EMPTY;
}