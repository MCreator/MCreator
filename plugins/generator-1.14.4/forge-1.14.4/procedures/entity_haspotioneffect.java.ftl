(new Object(){boolean check(LivingEntity _entity){
		if(_entity instanceof LivingEntity){
		Collection<EffectInstance> effects=_entity.getActivePotionEffects();
		for(EffectInstance effect:effects){
		if(effect.getPotion()== ${generator.map(field$potion, "potions")})
		return true;
		}
		}
		return false;
		}}.check((LivingEntity)${input$entity}))