<#if input$shooter == "null">
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
new Object() {
	public Projectile getProjectile(Level level, Entity shooter) {
		Projectile entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, level);
		entityToSpawn.setOwner(shooter);
		return entityToSpawn;
	}
}.getProjectile(projectileLevel, ${input$shooter})
</#if>