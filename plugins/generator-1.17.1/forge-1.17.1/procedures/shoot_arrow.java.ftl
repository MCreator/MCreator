if (${input$entity} instanceof LivingEntity _ent_sa && !_ent_sa.level.isClientSide()) {
	<#if field$rangeditem?has_content && field$rangeditem != "Arrow">
		${field$rangeditem}Entity.shoot(_ent_sa.level, _ent_sa, _ent_sa.level.getRandom(), ${opt.toFloat(input$speed)}, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)});
	<#else>
		Arrow entityToSpawn = new Arrow(_ent_sa.level, _ent_sa);
		entityToSpawn.shoot(_ent_sa.getLookAngle().x, _ent_sa.getLookAngle().y, _ent_sa.getLookAngle().z, ${opt.toFloat(input$speed)}, 0);
		entityToSpawn.setBaseDamage(${opt.toFloat(input$damage)});
		entityToSpawn.setKnockback(${opt.toInt(input$knockback)});
		_ent_sa.level.addFreshEntity(entityToSpawn);
	</#if>
}