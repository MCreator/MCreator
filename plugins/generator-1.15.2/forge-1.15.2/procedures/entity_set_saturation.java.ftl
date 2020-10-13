if ((${input$entity} instanceof PlayerEntity)) {
    ((PlayerEntity)${input$entity}).getFoodStats().setFoodSaturationLevel((float)${input$amount});
}