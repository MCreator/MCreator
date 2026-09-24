private static ItemStack getItemStackNbtFromEntity(Entity entity, String tagName) {
	return ItemStack.OPTIONAL_CODEC.parse(entity.level().registryAccess().createSerializationContext(NbtOps.INSTANCE), entity.getPersistentData().getCompoundOrEmpty(tagName)).result().orElse(ItemStack.EMPTY);
}