(new Object() {
    public boolean hasRecipe(Entity _ent, ResourceLocation recipe) {
        if (_ent instanceof ServerPlayerEntity)
            return ((ServerPlayerEntity)_ent).getRecipeBook().isUnlocked(recipe);
        else if (_ent.world.isRemote() && _ent instanceof ClientPlayerEntity)
            return ((ClientPlayerEntity)_ent).getRecipeBook().isUnlocked(recipe);
        return false;
    }
}.hasRecipe(${input$entity}, new ResourceLocation(${input$recipe})))