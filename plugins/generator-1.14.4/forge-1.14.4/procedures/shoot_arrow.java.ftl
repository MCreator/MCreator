if(world instanceof World && !world.getWorld().isRemote && ${input$entity} instanceof LivingEntity){
	<#if field$rangeditem?has_content && field$rangeditem != "Arrow">
		${field$rangeditem}Item.shoot(world.getWorld(), (LivingEntity) ${input$entity}, new Random(), (float) ${input$speed}, (float) ${input$damage}, (int) ${input$knockback}, ${input$pitch}, ${input$yaw});
	<#else>
		ArrowEntity entityToSpawn = new ArrowEntity(world.getWorld(), (LivingEntity) ${input$entity});
		entityToSpawn.shoot(${input$entity}, ${input$pitch}, ${input$yaw}, 0, (float) ${input$speed}, 0);
		entityToSpawn.setDamage((float) ${input$damage});
		entityToSpawn.setKnockbackStrength((int) ${input$knockback});
		world.addEntity(entityToSpawn);
	</#if>

}