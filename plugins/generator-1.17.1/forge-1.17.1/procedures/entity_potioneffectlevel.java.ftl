(${input$entity} instanceof LivingEntity _livEnt && _livEnt.hasEffect(${generator.map(field$potion, "effects")}) ?
    _livEnt.getEffect(${generator.map(field$potion, "effects")}).getAmplifier() : 0)