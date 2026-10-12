private static void setStackInSlot(ResourceHandler<ItemResource> handler, int index, ItemResource resource, int amount) {
	try (var tx = Transaction.openRoot()) {
		if (!handler.getResource(index).isEmpty())
			handler.extract(index, handler.getResource(index), handler.getAmountAsInt(index), tx);
		if (!resource.isEmpty() && amount > 0)
			handler.insert(index, resource, amount, tx);
		tx.commit();
	}
}