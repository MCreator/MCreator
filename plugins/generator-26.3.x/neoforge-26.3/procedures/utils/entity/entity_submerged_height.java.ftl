private static double getEntitySubmergedHeight(Entity entity) {
	for (FluidType fluidType : NeoForgeRegistries.FLUID_TYPES) {
		if (entity.level().getFluidState(entity.blockPosition()).getFluidType() == fluidType)
			return entity.getFluidTypeHeight(fluidType);
	}
	return 0;
}