private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
	BlockEntity blockEntity = world.getBlockEntity(pos);
	if (blockEntity != null)
		return blockEntity.getPersistentData().getDoubleOr(tag, 0);
	return -1;
}