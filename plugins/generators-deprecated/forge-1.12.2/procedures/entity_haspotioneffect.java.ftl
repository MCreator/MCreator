(new Object(){boolean check(){
		if(entity instanceof EntityLivingBase){
		Collection<PotionEffect> effects=((EntityLivingBase)entity).getActivePotionEffects();
		for(PotionEffect effect:effects){
		if(effect.getPotion()== ${generator.map(field$potion, "potions")})
		return true;
		}
		}
		return false;
		}}.check())