(new Object() {
	public int getSpawnY(IWorld _world, Entity _ent) {
		if (_ent instanceof ServerPlayerEntity)
			if (((ServerPlayerEntity) _ent).func_241140_K_() != null)
				return ((ServerPlayerEntity) _ent).func_241140_K_().getY();
		else if (_world instanceof ClientWorld)
			return ((ClientWorld) _world).func_239140_u_().getY();
		return _world.getWorldInfo().getSpawnY();
	}
}.getSpawnY(world, ${input$entity}))