private static boolean isInWaterRainOrBubble(Entity entity) {
	return entity.isInWaterOrRain() || entity.getInBlockState().is(Blocks.BUBBLE_COLUMN);
}