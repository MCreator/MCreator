private static String getBlockNBTString(LevelAccessor world, BlockPos pos, String tag) {
	BlockEntity blockEntity = world.getBlockEntity(pos);
	if (blockEntity != null)
		return blockEntity.getPersistentData().getString(tag);
	return "";
}