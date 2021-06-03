(new Object() {
    public boolean hasRecipe(Entity _ent, ResourceLocation recipe) {
        if (_ent instanceof ServerPlayerEntity)
            return ((ServerPlayerEntity)_ent).getRecipeBook().func_226144_b_(recipe);
        else if (_ent.world.isRemote() && _ent instanceof ClientPlayerEntity)
            return ((ClientPlayerEntity)_ent).getRecipeBook().func_226144_b_(recipe);
        return false;
    }
}.hasRecipe(${input$entity}, new ResourceLocation(${input$recipe})))