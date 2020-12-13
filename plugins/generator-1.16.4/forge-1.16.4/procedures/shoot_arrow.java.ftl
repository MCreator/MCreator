if (world instanceof World && !((World) world).getWorld().isRemote && ${input$entity} instanceof LivingEntity) {
	<#if field$rangeditem?has_content && field$rangeditem != "Arrow">
		${field$rangeditem}Item.shoot(((World) world).getWorld(), (LivingEntity) ${input$entity}, new Random(), (float) ${input$speed}, (float) ${input$damage}, (int) ${input$knockback});
	<#else>
		ArrowEntity entityToSpawn = new ArrowEntity(((World) world).getWorld(), (LivingEntity) ${input$entity});
		entityToSpawn.shoot(${input$entity}.getLookVec().x, ${input$entity}.getLookVec().y, ${input$entity}.getLookVec().z, (float) ${input$speed}, 0);
		entityToSpawn.setDamage((float) ${input$damage});
		entityToSpawn.setKnockbackStrength((int) ${input$knockback});
		world.addEntity(entityToSpawn);
	</#if>
}