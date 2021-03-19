if (${input$entity} instanceof LivingEntity) {
    ((LivingEntity) ${input$entity}).removePotionEffect(${generator.map(field$potion, "potions")});
}