(new Object() {
	public int getSpawnZ(IWorld _world, Entity _ent) {
		if (_ent instanceof ServerPlayerEntity) {
			if (((ServerPlayerEntity) _ent).func_241140_K_() != null) {
				return ((ServerPlayerEntity) _ent).func_241140_K_().getZ();
		else if (_world instanceof ClientWorld)
			return ((ClientWorld) _world).func_239140_u_().getZ();
		else
			return _world.getWorldInfo().getSpawnZ();
	}
}.getSpawnZ(world, ${input$entity}))