(${input$entity} instanceof LivingEntity _ent_effLvl && _ent_effLvl.hasEffect(${generator.map(field$potion, "effects")}) ?
    _ent_effLvl.getEffect(${generator.map(field$potion, "effects")}).getAmplifier() : 0)