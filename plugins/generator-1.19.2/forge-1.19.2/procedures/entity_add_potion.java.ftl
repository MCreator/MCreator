if(${input$entity} instanceof LivingEntity _entity)
	_entity.addEffect(new MobEffectInstance(${generator.map(field$potion, "effects")},${opt.toInt(input$duration)},${opt.toInt(input$level)}));