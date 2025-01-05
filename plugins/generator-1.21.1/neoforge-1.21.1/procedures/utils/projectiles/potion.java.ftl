private static Projectile createPotionProjectile(Level level, ItemStack contents, Entity shooter, Vec3 acceleration) {
	ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
	entityToSpawn.setItem(contents);
	if (shooter != null)
		entityToSpawn.setOwner(shooter);
	if (Vec3.ZERO.equals(acceleration)) {
		entityToSpawn.setDeltaMovement(acceleration);
		entityToSpawn.hasImpulse = true;
	}
	return entityToSpawn;
}