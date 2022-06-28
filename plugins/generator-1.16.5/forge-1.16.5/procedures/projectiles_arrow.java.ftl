<#assign projectile = generator.map(field$projectile, "projectiles", 1)>
<#assign hasShooter = (input$shooter != "null")>
<#assign isPiercing = (input$piercing != "/*@int*/0")>
new Object() {
	public ProjectileEntity getArrow(World world<#if hasShooter>, Entity shooter</#if>, float damage, int knockback<#if isPiercing>, byte piercing</#if>) {
		AbstractArrowEntity entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${projectile}, world);
		<#if hasShooter>entityToSpawn.setShooter(shooter);</#if>
		entityToSpawn.setDamage(damage);
		entityToSpawn.setKnockbackStrength(knockback);
		<#if isPiercing>entityToSpawn.setPierceLevel(piercing);</#if>
		<#if field$fire?lower_case == "true">entityToSpawn.setFire(100);</#if>
		<#if field$particles?lower_case == "true">entityToSpawn.setIsCritical(true);</#if>
		<#if field$pickup != "DISALLOWED">entityToSpawn.pickupStatus = AbstractArrowEntity.PickupStatus.${field$pickup};</#if>
		return entityToSpawn;
}}.getArrow(projectileLevel<#if hasShooter>, ${input$shooter}</#if>, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)}<#if isPiercing>, (byte) ${input$piercing}</#if>)