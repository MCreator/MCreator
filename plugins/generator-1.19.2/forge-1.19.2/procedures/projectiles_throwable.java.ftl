<#if input$shooter == "null">
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
new Object() {
	public Projectile getProjectile(Level _level, Entity _shooter) {
		Projectile _entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, _level);
		_entityToSpawn.setOwner(_shooter);
		return _entityToSpawn;
}}.getProjectile(projectileLevel, ${input$shooter})
</#if>