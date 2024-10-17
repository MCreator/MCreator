<#assign projectile = generator.map(field$projectile, "projectiles", 1)>
<#assign hasShooter = (input$shooter != "null")>
<#assign isPiercing = (input$piercing != "/*@int*/0")>
new Object() {
	public Projectile getArrow(Level level<#if hasShooter>, Entity shooter</#if>, float damage, int knockback<#if isPiercing>, byte piercing</#if>) {
		AbstractArrow entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${projectile}, level);
		<#if hasShooter>entityToSpawn.setOwner(shooter);</#if>
		entityToSpawn.setBaseDamage(damage);
		entityToSpawn.setKnockback(knockback);
		<#if field$projectile?starts_with("CUSTOM:")>entityToSpawn.setSilent(true);</#if>
		<#if isPiercing>entityToSpawn.setPierceLevel(piercing);</#if>
		<#if field$fire == "TRUE">entityToSpawn.setSecondsOnFire(100);</#if>
		<#if field$particles == "TRUE">entityToSpawn.setCritArrow(true);</#if>
		<#if field$pickup != "DISALLOWED">entityToSpawn.pickup = AbstractArrow.Pickup.${field$pickup};</#if>
		return entityToSpawn;
	}
}.getArrow(projectileLevel<#if hasShooter>, ${input$shooter}</#if>, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)}<#if isPiercing>, (byte) ${input$piercing}</#if>)