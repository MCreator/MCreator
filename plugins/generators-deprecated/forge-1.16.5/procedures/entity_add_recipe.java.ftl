<#include "mcelements.ftl">
if(${input$entity} instanceof ServerPlayerEntity) {
    ((ServerPlayerEntity)${input$entity}).unlockRecipes(new ResourceLocation[]{${toResourceLocation(input$recipe)}});
}