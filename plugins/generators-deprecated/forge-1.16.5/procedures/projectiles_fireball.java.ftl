<#assign hasShooter = (input$shooter != "null")>
<#assign hasAcceleration = (input$ax != "/*@int*/0") || (input$ay != "/*@int*/0") || (input$az != "/*@int*/0")>
<#if (!hasShooter) && (!hasAcceleration)>
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
new Object() {
	public ProjectileEntity getFireball(World world<#if hasShooter>, Entity shooter</#if><#if hasAcceleration>, double ax, double ay, double az</#if>) {
		DamagingProjectileEntity entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, world);
		<#if hasShooter>entityToSpawn.setShooter(shooter);</#if>
		<#if hasAcceleration>
		entityToSpawn.accelerationX = ax;
		entityToSpawn.accelerationY = ay;
		entityToSpawn.accelerationZ = az;
		</#if>
		return entityToSpawn;
}}.getFireball(projectileLevel<#if hasShooter>, ${input$shooter}</#if><#if hasAcceleration>, ${input$ax}, ${input$ay}, ${input$az}</#if>)
</#if>