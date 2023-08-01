<#include "mcelements.ftl">
(new Object() {
    public boolean hasRecipe(Entity _ent, ResourceLocation recipe) {
        if (_ent instanceof ServerPlayer _player)
            return _player.getRecipeBook().contains(recipe);
        else if (_ent.level().isClientSide() && _ent instanceof LocalPlayer _player)
            return _player.getRecipeBook().contains(recipe);
        return false;
    }
}.hasRecipe(${input$entity}, ${toResourceLocation(input$recipe)}))