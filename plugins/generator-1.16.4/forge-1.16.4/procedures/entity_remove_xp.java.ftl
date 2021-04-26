if(${input$entity} instanceof PlayerEntity){
    ((PlayerEntity) ${input$entity}).giveExperiencePoints((int)-${input$amount});
}