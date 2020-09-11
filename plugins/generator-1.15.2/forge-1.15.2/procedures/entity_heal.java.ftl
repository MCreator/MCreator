if (${input$entity} instanceof LivingEntity) {
    ((LivingEntity) ${input$entity}).setHealth(((LivingEntity) ${input$entity}).getHealth() + ${input$amount}f);
}