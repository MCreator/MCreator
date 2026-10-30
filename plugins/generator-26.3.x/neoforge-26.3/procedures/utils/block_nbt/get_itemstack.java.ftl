private static ItemStack getBlockNBTItemStack(LevelAccessor world, BlockPos pos, String tag) {
	BlockEntity blockEntity = world.getBlockEntity(pos);
	if (blockEntity != null)
		return ItemStack.OPTIONAL_CODEC.parse(world.registryAccess().createSerializationContext(NbtOps.INSTANCE), blockEntity.getPersistentData().getCompoundOrEmpty(tag)).result().orElse(ItemStack.EMPTY);
	return ItemStack.EMPTY;
}