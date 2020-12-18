if (${input$entity} instanceof LivingEntity) {
	Entity _ent = ${input$entity};
	if(!_ent.world.isRemote()) {
		<#if field$rangeditem?has_content && field$rangeditem != "Arrow">
			${field$rangeditem}Item.shoot(_ent.world, (LivingEntity) ${input$entity}, new Random(), (float) ${input$speed}, (float) ${input$damage}, (int) ${input$knockback});
		<#else>
			ArrowEntity entityToSpawn = new ArrowEntity(_ent.world, (LivingEntity) ${input$entity});
			entityToSpawn.shoot(${input$entity}.getLookVec().x, ${input$entity}.getLookVec().y, ${input$entity}.getLookVec().z, (float) ${input$speed}, 0);
			entityToSpawn.setDamage((float) ${input$damage});
			entityToSpawn.setKnockbackStrength((int) ${input$knockback});
			_ent.world.addEntity(entityToSpawn);
		</#if>
	}
}