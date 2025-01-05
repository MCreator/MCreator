private static Projectile initThrowableProjectile(Projectile entityToSpawn, Entity shooter, Vec3 acceleration) {
	if (shooter != null)
		entityToSpawn.setOwner(shooter);
	if (Vec3.ZERO.equals(acceleration)) {
		entityToSpawn.setDeltaMovement(acceleration);
		entityToSpawn.hasImpulse = true;
	}
	return entityToSpawn;
}