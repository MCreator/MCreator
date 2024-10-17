if(${input$entity} instanceof PlayerEntity) {
    ((PlayerEntity)${input$entity}).abilities.allowFlying = ${input$condition};
    ((PlayerEntity)${input$entity}).sendPlayerAbilities();
}