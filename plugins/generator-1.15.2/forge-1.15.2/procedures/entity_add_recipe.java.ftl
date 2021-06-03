if(${input$entity} instanceof ServerPlayerEntity) {
    ((ServerPlayerEntity)${input$entity}).unlockRecipes(new ResourceLocation[]{new ResourceLocation(${input$recipe})});
}