if(${input$entity} instanceof PlayerEntity) {
    ((PlayerEntity)${input$entity}).abilities.isFlying = ${input$condition};
    ((PlayerEntity)${input$entity}).sendPlayerAbilities();
}