private static void setStackInSlot(ResourceHandler<ItemResource> handler, int index, ItemResource resource, int amount) {
	if (!handler.getResource(index).isEmpty())
		try (var tx = Transaction.openRoot()) {
			handler.extract(index, handler.getResource(index), handler.getAmountAsInt(index), tx);
			tx.commit();
		}
	if (!resource.isEmpty() && amount > 0)
		ItemUtil.insertItemReturnRemaining(handler, index, resource.toStack(amount), false, null);
}