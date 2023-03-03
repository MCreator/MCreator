<#assign projectile = generator.map(field$projectile, "projectiles", 1)>
<#assign hasShooter = (input$shooter != "null")>
<#assign isPiercing = (input$piercing != "/*@int*/0")>
new Object() {
	public Projectile getArrow(Level _level<#if hasShooter>, Entity _shooter</#if>, float _damage, int _knockback<#if isPiercing>, byte _piercing</#if>) {
		AbstractArrow _entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${projectile}, _level);
		<#if hasShooter>_entityToSpawn.setOwner(_shooter);</#if>
		_entityToSpawn.setBaseDamage(_damage);
		_entityToSpawn.setKnockback(_knockback);
		<#if field$projectile?starts_with("CUSTOM:")>_entityToSpawn.setSilent(true);</#if>
		<#if isPiercing>_entityToSpawn.setPierceLevel(_piercing);</#if>
		<#if field$fire == "TRUE">_entityToSpawn.setSecondsOnFire(100);</#if>
		<#if field$particles == "TRUE">_entityToSpawn.setCritArrow(true);</#if>
		<#if field$pickup != "DISALLOWED">_entityToSpawn.pickup = AbstractArrow.Pickup.${field$pickup};</#if>
		return _entityToSpawn;
}}.getArrow(projectileLevel<#if hasShooter>, ${input$shooter}</#if>, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)}<#if isPiercing>, (byte) ${input$piercing}</#if>)