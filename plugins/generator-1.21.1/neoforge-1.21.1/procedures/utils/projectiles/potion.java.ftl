<@addTemplate file="utils/projectiles/projectile.java.ftl"/>
private static Projectile createPotionProjectile(Level level, ItemStack contents, Entity shooter, Vec3 acceleration) {
	ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
	entityToSpawn.setItem(contents);
	return initProjectileProperties(entityToSpawn, shooter, acceleration);
}