(new Object() {
	public int getSpawnX(IWorld _world, Entity _ent) {
		if (_ent instanceof PlayerEntity) {
			Optional<BlockPos> _bp = ((PlayerEntity) _ent).getBedPosition();
			if (_bp.isPresent())
				retval = _bp.get().getX();
		}
		return _world.getWorldInfo().getSpawnX();
	}
}.getSpawnX(world, ${input$entity}))