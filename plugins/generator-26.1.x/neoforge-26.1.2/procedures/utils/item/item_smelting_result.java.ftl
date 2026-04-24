private static ItemStack getItemStackSmeltingResult(LevelAccessor level, ItemStack input) {
	SingleRecipeInput recipeInput = new SingleRecipeInput(input);
	if (level instanceof ServerLevel serverLevel) {
		return serverLevel.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, serverLevel)
				.map(recipe -> recipe.value().assemble(recipeInput).copy()).orElse(ItemStack.EMPTY);
	}
	return ItemStack.EMPTY;
}