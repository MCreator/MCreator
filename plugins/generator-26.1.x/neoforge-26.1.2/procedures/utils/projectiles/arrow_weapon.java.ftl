private static ItemStack createArrowWeaponItemStack(Level level, int knockback, byte piercing) {
	ItemStack weapon = new ItemStack(Items.ARROW);
	if (knockback > 0)
		weapon.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK), knockback);
	if (piercing > 0)
		weapon.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.PIERCING), piercing);
	return weapon;
}