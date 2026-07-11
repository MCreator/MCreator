private static boolean isInWaterOrBubble(Entity entity) {
	return entity.isInWater() || entity.getInBlockState().is(Blocks.BUBBLE_COLUMN);
}