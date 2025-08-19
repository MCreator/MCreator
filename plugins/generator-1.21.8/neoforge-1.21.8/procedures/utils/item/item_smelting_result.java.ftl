private static ItemStack getItemStackFromItemStackSlot(LevelAccessor level, ItemStack input) {
	SingleRecipeInput recipeInput = new SingleRecipeInput(input);
	if (level instanceof ServerLevel serverLevel) {
		return serverLevel.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, serverLevel)
				.map(recipe -> recipe.value().assemble(recipeInput, serverLevel.registryAccess()).copy()).orElse(ItemStack.EMPTY);
	}
	return ItemStack.EMPTY;
}