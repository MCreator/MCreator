new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel) {
	private final Entity shooter = ${input$shooter};
	private final double damage = ${opt.toFloat(input$damage)}, knockback = ${opt.toInt(input$knockback)};
	private final byte piercing = (byte) ${input$piercing};

	{
		<#if input$shooter != "null">setOwner(shooter);</#if>
		setBaseDamage(damage);
		<#if field$projectile?starts_with("CUSTOM:")>setSilent(true);</#if>
		<#if field$fire == "TRUE">igniteForSeconds(100);</#if>
		<#if field$particles == "TRUE">setCritArrow(true);</#if>
		<#if field$pickup != "DISALLOWED">this.pickup = AbstractArrow.Pickup.${field$pickup};</#if>
	}

	@Override public byte getPierceLevel() {
		return piercing;
	}

	@Override protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
		if (knockback > 0) {
			double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
			if (vec3.lengthSqr() > 0.0) {
				livingEntity.push(vec3.x, 0.1, vec3.z);
			}
		}
	}
}