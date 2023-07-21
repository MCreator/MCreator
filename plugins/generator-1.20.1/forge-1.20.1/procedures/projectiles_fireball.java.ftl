<#assign hasShooter = (input$shooter != "null")>
<#assign hasAcceleration = (input$ax != "/*@int*/0") || (input$ay != "/*@int*/0") || (input$az != "/*@int*/0")>
<#if (!hasShooter) && (!hasAcceleration)>
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
new Object() {
	public Projectile getFireball(Level level<#if hasShooter>, Entity shooter</#if><#if hasAcceleration>, double ax, double ay, double az</#if>) {
		AbstractHurtingProjectile entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, level);
		<#if hasShooter>entityToSpawn.setOwner(shooter);</#if>
		<#if hasAcceleration>
		entityToSpawn.xPower = ax;
		entityToSpawn.yPower = ay;
		entityToSpawn.zPower = az;
		</#if>
		return entityToSpawn;
	}
}.getFireball(projectileLevel<#if hasShooter>, ${input$shooter}</#if><#if hasAcceleration>, ${input$ax}, ${input$ay}, ${input$az}</#if>)
</#if>