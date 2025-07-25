private static Projectile initProjectileProperties(Projectile entityToSpawn, Entity shooter, Vec3 acceleration) {
	entityToSpawn.setOwner(shooter);
	if (!Vec3.ZERO.equals(acceleration)) {
		entityToSpawn.setDeltaMovement(acceleration);
		entityToSpawn.hasImpulse = true;
	}
	return entityToSpawn;
}