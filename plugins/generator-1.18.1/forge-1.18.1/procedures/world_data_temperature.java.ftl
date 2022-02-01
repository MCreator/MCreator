/*@float*/(new Object() {
	public float getBiomeTemperature(LevelAccessor _level, BlockPos _bPos) {
		try {
			return ObfuscationReflectionHelper.findMethod(Biome.class, "m_47505_", BlockPos.class).invoke(_level.getBiome(_bPos), _bPos);
		} catch (Exception ignored) {
			return 0F;
		}
	}
}.getBiomeTemperature(world, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z})))