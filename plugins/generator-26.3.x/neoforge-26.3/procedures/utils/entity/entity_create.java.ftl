@Nullable private static Entity createStaticEntity(EntityType<?> entityType, Level level) {
	if (entityType != null) {
		Entity entity = entityType.create(level, EntitySpawnReason.EVENT);
		if (entity != null)
			entity.setId(level.getRandom().nextInt());

		return entity;
	}

	return null;
}