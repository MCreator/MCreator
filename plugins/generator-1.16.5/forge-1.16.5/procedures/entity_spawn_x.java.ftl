(new Object() {
	public int getSpawnX(IWorld _world, Entity _ent) {
		if (_ent instanceof ServerPlayerEntity)
			if (((ServerPlayerEntity) _ent).func_241140_K_() != null)
				return ((ServerPlayerEntity) _ent).func_241140_K_().getX();
		else if (_world instanceof ClientWorld)
			return ((ClientWorld) _world).func_239140_u_().getX();
		else
			return _world.getWorldInfo().getSpawnX();
	}
}.getSpawnX(world, ${input$entity}))