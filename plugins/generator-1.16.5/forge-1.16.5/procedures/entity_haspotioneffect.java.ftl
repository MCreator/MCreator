(new Object(){boolean check(Entity _entity){
		if(_entity instanceof LivingEntity){
		Collection<EffectInstance> effects=((LivingEntity)_entity).getActivePotionEffects();
		for(EffectInstance effect:effects){
		if(effect.getPotion()== ${generator.map(field$potion, "potions")})
		return true;
		}
		}
		return false;
		}}.check(${input$entity}))