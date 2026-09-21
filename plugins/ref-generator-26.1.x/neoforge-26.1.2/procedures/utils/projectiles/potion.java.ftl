<@addTemplate file="utils/projectiles/projectile.java.ftl"/>
private static Projectile createPotionProjectile(Level level, ItemStack contents, Entity shooter, Vec3 acceleration) {
	AbstractThrownPotion entityToSpawn =
			contents.getItem() == Items.LINGERING_POTION ?
					new ThrownLingeringPotion(EntityType.LINGERING_POTION, level) :
					new ThrownSplashPotion(EntityType.SPLASH_POTION, level);
	entityToSpawn.setItem(contents);
	return initProjectileProperties(entityToSpawn, shooter, acceleration);
}