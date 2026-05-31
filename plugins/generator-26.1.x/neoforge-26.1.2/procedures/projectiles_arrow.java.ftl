<@addTemplate file="utils/projectiles/arrow.java.ftl"/>
<#if (input$knockback == "/*@int*/0") && (input$piercing == "/*@int*/0")>
	initArrowProjectile(new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel),
		${input$shooter}, ${opt.toFloat(input$damage)}, ${field$projectile?starts_with("CUSTOM:")}, ${field$fire == "TRUE"}, ${field$particles == "TRUE"},
		AbstractArrow.Pickup.${field$pickup})
<#elseif field$projectile?starts_with("CUSTOM:")>
	<@addTemplate file="utils/projectiles/arrow_weapon.java.ftl"/>
	initArrowProjectile(new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, 0, 0, 0, projectileLevel,
		createArrowWeaponItemStack(projectileLevel, ${opt.toInt(input$knockback)}, (byte) ${input$piercing})), ${input$shooter}, ${opt.toFloat(input$damage)},
		true, ${field$fire == "TRUE"}, ${field$particles == "TRUE"}, AbstractArrow.Pickup.${field$pickup})
<#else>
	<@addTemplate file="utils/projectiles/arrow_weapon.java.ftl"/>
	initArrowProjectile(new ${generator.map(field$projectile, "projectiles", 0)}(projectileLevel, 0, 0, 0,
		new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel).getPickupItemStackOrigin(),
		createArrowWeaponItemStack(projectileLevel, ${opt.toInt(input$knockback)}, (byte) ${input$piercing})), ${input$shooter}, ${opt.toFloat(input$damage)},
		false, ${field$fire == "TRUE"}, ${field$particles == "TRUE"}, AbstractArrow.Pickup.${field$pickup})
</#if>