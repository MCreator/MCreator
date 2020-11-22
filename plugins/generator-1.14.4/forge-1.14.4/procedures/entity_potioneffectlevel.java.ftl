(new Object(){int check(Entity _entity){
		if(_entity instanceof LivingEntity){
		Collection<EffectInstance> effects=((LivingEntity)_entity).getActivePotionEffects();
		for(EffectInstance effect:effects){
		if(effect.getPotion()== ${generator.map(field$potion, "potions")})
		return effect.getAmplifier();
		}
		}
		return 0;
		}}.check(${input$entity}))