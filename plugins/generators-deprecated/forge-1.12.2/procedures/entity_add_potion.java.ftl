if(entity instanceof EntityLivingBase)
	((EntityLivingBase)entity).addPotionEffect(new PotionEffect(${generator.map(field$potion, "potions")},(int) ${input$duration},(int) ${input$level}));