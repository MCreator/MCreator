if (${input$entity} instanceof LivingEntity) {
    DamageSource a = new DamageSource(${input$localization_text}).setDamageBypassesArmor();
    ((LivingEntity) ${input$entity}).attackEntityFrom(a, (int) ${input$damage_number});
}