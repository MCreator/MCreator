if (${input$entity} instanceof LivingEntity) {
    ((LivingEntity) ${input$entity}).attackEntityFrom(new DamageSource(${input$localization_text}).setDamageBypassesArmor(), (float) ${input$damage_number});
}
