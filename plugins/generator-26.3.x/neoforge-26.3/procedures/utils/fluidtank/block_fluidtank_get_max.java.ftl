private static int getFluidTankCapacity(LevelAccessor level, BlockPos pos, int tank, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Fluid.BLOCK, pos, direction) instanceof ResourceHandler<FluidResource> fluidHandler)
			return fluidHandler.getCapacityAsInt(tank, FluidResource.EMPTY);
	}
	return 0;
}