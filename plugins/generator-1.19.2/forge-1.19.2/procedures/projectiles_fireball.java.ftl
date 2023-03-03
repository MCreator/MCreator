<#assign hasShooter = (input$shooter != "null")>
<#assign hasAcceleration = (input$ax != "/*@int*/0") || (input$ay != "/*@int*/0") || (input$az != "/*@int*/0")>
<#if (!hasShooter) && (!hasAcceleration)>
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
new Object() {
	public Projectile getFireball(Level _level<#if hasShooter>, Entity _shooter</#if><#if hasAcceleration>, double _ax, double _ay, double _az</#if>) {
		AbstractHurtingProjectile _entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, _level);
		<#if hasShooter>_entityToSpawn.setOwner(_shooter);</#if>
		<#if hasAcceleration>
		_entityToSpawn.xPower = _ax;
		_entityToSpawn.yPower = _ay;
		_entityToSpawn.zPower = _az;
		</#if>
		return _entityToSpawn;
}}.getFireball(projectileLevel<#if hasShooter>, ${input$shooter}</#if><#if hasAcceleration>, ${input$ax}, ${input$ay}, ${input$az}</#if>)
</#if>