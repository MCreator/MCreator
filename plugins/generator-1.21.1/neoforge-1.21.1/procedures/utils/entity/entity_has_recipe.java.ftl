private static boolean hasEntityRecipe(Entity entity, ResourceLocation recipe) {
	if (entity instanceof ServerPlayer player)
		return player.getRecipeBook().contains(recipe);
	else if (entity instanceof LocalPlayer player && player.level().isClientSide())
		return player.getRecipeBook().contains(recipe);
	return false;
}