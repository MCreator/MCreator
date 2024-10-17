if(${input$entity} instanceof PlayerEntity) {
    ((PlayerEntity)${input$entity}).abilities.disableDamage = ${input$condition};
    ((PlayerEntity)${input$entity}).sendPlayerAbilities();
}