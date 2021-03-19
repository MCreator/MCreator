if(${input$entity} instanceof PlayerEntity) {
    ((PlayerEntity)${input$entity}).abilities.allowEdit = ${input$condition};
    ((PlayerEntity)${input$entity}).sendPlayerAbilities();
}