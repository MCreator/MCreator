new Object() {
    public void removeRecipe(Entity _ent, ResourceLocation recipe) {
        if (_ent instanceof ServerPlayerEntity)
            ((ServerPlayerEntity)_ent).world.getRecipeManager().getRecipe(recipe)
                .ifPresent(_rec -> ((ServerPlayerEntity)_ent).resetRecipes(Collections.singleton(_rec)));
    }
}.removeRecipe(${input$entity}, new ResourceLocation(${input$recipe}));