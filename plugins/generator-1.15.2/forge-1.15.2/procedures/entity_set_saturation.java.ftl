if ((${input$entity} instanceof PlayerEntity)) {
    ((PlayerEntity)${input$entity}).getFoodStats().setFoodSaturationLevel(${input$amount});
}