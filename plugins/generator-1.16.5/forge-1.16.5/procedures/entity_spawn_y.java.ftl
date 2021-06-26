(new Object() {
	public int getSpawnY(IWorld _world, Entity _ent) {
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				return _bp.get().getY();
		}
		return _world.getWorldInfo().getSpawnY();
	}
}.getSpawnY(world, ${input$entity}))