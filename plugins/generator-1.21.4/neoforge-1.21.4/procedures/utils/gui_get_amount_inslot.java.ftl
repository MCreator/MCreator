private static int getAmountInGUISlot(Entity entity, int sltid) {
	if(entity instanceof Player player && player.containerMenu instanceof Supplier slotSupplier && slotSupplier.get() instanceof Map guiSlots) {
		ItemStack stack = ((Slot) guiSlots.get(sltid)).getItem();
		if(stack != null)
			return stack.getCount();
	}
	return 0;
}