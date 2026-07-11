private static Property<?> getPropertyByName(BlockState state, String name) {
	for (Property<?> property : state.getProperties()) {
		if (property.getName().equals(name)) {
			return property;
		}
	}
	return null;
}