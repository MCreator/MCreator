<#assign projectile = generator.map(field$rangeditem, "projectiles", 0)>
if (${input$entity} instanceof LivingEntity) {
	LivingEntity _ent = (LivingEntity) ${input$entity};
	if(!_ent.world.isRemote()) {
		<#if field$rangeditem?has_content && field$rangeditem?starts_with("CUSTOM:")>
			${field$rangeditem?remove_beginning("CUSTOM:")}Item.shoot(_ent.world, _ent, new Random(), ${opt.toFloat(input$speed)}, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)});
		<#else>
			AbstractArrowEntity entityToSpawn = new ${projectile}(_ent.world, _ent);
			entityToSpawn.shoot(_ent.getLookVec().x, _ent.getLookVec().y, _ent.getLookVec().z, ${opt.toFloat(input$speed)}, 0);
			entityToSpawn.setDamage(${opt.toFloat(input$damage)});
			<#if input$knockback != "/*@int*/0">
			entityToSpawn.setKnockbackStrength(${opt.toInt(input$knockback)});
			</#if>
			_ent.world.addEntity(entityToSpawn);
		</#if>
	}
}