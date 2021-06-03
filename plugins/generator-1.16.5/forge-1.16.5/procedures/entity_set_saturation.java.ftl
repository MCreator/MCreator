if (${input$entity} instanceof PlayerEntity) {
    ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, ((PlayerEntity) ${input$entity}).getFoodStats(), (float)${input$amount}, "field_75125_b");
}