if (${input$entity} instanceof LivingEntity _ranged && !_ranged.level.isClientSide()) {
	<#if field$rangeditem?has_content && field$rangeditem != "Arrow">
		${field$rangeditem}Entity.shoot(_ranged.level, _ranged, _ranged.level.getRandom(), ${opt.toFloat(input$speed)}, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)});
	<#else>
		Arrow entityToSpawn = new Arrow(_ranged.level, _ranged);
		entityToSpawn.shoot(_ranged.getLookAngle().x, _ranged.getLookAngle().y, _ranged.getLookAngle().z, ${opt.toFloat(input$speed)}, 0);
		entityToSpawn.setBaseDamage(${opt.toFloat(input$damage)});
		entityToSpawn.setKnockback(${opt.toInt(input$knockback)});
		_ranged.level.addFreshEntity(entityToSpawn);
	</#if>
}