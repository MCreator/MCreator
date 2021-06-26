(new Object() {
	public int getSpawnZ(IWorld _world, Entity _ent) {
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				return _bp.get().getZ();
		}
		return _world.getWorldInfo().getSpawnZ();
	}
}.getSpawnZ(world, ${input$entity}))