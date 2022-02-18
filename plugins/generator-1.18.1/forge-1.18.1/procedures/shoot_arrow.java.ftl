if (${input$entity} instanceof LivingEntity _shooter && !_shooter.level.isClientSide()) {
	<#if field$rangeditem?has_content && field$rangeditem != "Arrow">
		${field$rangeditem}Entity.shoot(_shooter.level, _shooter, _shooter.level.getRandom(), ${opt.toFloat(input$speed)}, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)});
	<#else>
		Arrow entityToSpawn = new Arrow(_shooter.level, _shooter);
		entityToSpawn.shoot(_shooter.getLookAngle().x, _shooter.getLookAngle().y, _shooter.getLookAngle().z, ${opt.toFloat(input$speed)}, 0);
		entityToSpawn.setBaseDamage(${opt.toFloat(input$damage)});
		entityToSpawn.setKnockback(${opt.toInt(input$knockback)});
		_shooter.level.addFreshEntity(entityToSpawn);
	</#if>
}