@Nullable private static Entity createStaticEntity(EntityType<?> entityType, LevelAccessor world) {
	if (entityType != null && world instanceof Level level) {
		Entity entity = entityType.create(level, EntitySpawnReason.EVENT);
		if (entity != null && level.isClientSide())
			entity.setId(-1);

		return entity;
	}

	return null;
}