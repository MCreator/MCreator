private static boolean hasEntityInInventory(Entity entity, ItemStack itemstack) {
	if (entity instanceof Player player)
		return player.getInventory().contains(stack -> !stack.isEmpty() && ItemStack.isSameItem(stack, itemstack));
	return false;
}