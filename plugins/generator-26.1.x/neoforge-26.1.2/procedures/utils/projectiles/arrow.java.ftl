private static AbstractArrow initArrowProjectile(AbstractArrow entityToSpawn, Entity shooter, float damage,
		boolean silent, boolean fire, boolean particles, AbstractArrow.Pickup pickup) {
	entityToSpawn.setOwner(shooter);
	entityToSpawn.setBaseDamage(damage);
	if (silent)
		entityToSpawn.setSilent(true);
	if (fire)
		entityToSpawn.igniteForSeconds(100);
	if (particles)
		entityToSpawn.setCritArrow(true);
	entityToSpawn.pickup = pickup;
	return entityToSpawn;
}