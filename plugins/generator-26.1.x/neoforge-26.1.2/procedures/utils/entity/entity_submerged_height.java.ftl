private static double getEntitySubmergedHeight(Entity entity) {
	return Math.max(entity.getFluidHeight(FluidTags.WATER), entity.getFluidHeight(FluidTags.LAVA));
}