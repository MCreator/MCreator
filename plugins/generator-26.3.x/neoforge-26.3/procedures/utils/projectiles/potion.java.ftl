<@addTemplate file="utils/projectiles/projectile.java.ftl"/>
private static Projectile createPotionProjectile(Level level, ItemStack contents, Entity shooter, Vec3 acceleration) {
	AbstractThrownPotion entityToSpawn =
			contents.getItem() == Items.LINGERING_POTION ?
					new ThrownLingeringPotion(EntityTypes.LINGERING_POTION, level) :
					new ThrownSplashPotion(EntityTypes.SPLASH_POTION, level);
	entityToSpawn.setItem(contents);
	return initProjectileProperties(entityToSpawn, shooter, acceleration);
}