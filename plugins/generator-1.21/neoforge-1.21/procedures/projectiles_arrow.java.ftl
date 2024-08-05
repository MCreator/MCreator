<#assign projectile = generator.map(field$projectile, "projectiles", 1)>
<#assign hasShooter = (input$shooter != "null")>
<#assign isPiercing = (input$piercing != "/*@int*/0")>
new Object() {
	public Projectile getArrow(Level level<#if hasShooter>, Entity shooter</#if>, float damage, int knockback, byte piercing) {
		AbstractArrow entityToSpawn = new ${generator.map(field$projectile, "projectiles", 0)}(${projectile}, level) {
			@Override public byte getPierceLevel() {
				return piercing;
			}

			@Override protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
				if (knockback > 0) {
					double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE));
					Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
					if (vec3.lengthSqr() > 0.0) {
						livingEntity.push(vec3.x, 0.1, vec3.z);
					}
				}
			}
		};
		<#if hasShooter>entityToSpawn.setOwner(shooter);</#if>
		entityToSpawn.setBaseDamage(damage);
		<#if field$projectile?starts_with("CUSTOM:")>entityToSpawn.setSilent(true);</#if>
		<#if field$fire == "TRUE">entityToSpawn.igniteForSeconds(100);</#if>
		<#if field$particles == "TRUE">entityToSpawn.setCritArrow(true);</#if>
		<#if field$pickup != "DISALLOWED">entityToSpawn.pickup = AbstractArrow.Pickup.${field$pickup};</#if>
		return entityToSpawn;
	}
}.getArrow(projectileLevel<#if hasShooter>, ${input$shooter}</#if>, ${opt.toFloat(input$damage)}, ${opt.toInt(input$knockback)}, (byte) ${input$piercing})