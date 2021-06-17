<#assign projectile = generator.map(field$rangeditem, "projectiles", 0)>
if (${input$entity} instanceof LivingEntity) {
	LivingEntity _ent = (LivingEntity) ${input$entity};
	if(!_ent.world.isRemote()) {
		AbstractArrowEntity entityToSpawn = new ${projectile}(_ent.world, _ent);
		entityToSpawn.shoot(_ent.getLookVec().x, _ent.getLookVec().y, _ent.getLookVec().z, (float) ${input$speed}, 0);
		entityToSpawn.setDamage((float) ${input$damage});
		entityToSpawn.setKnockbackStrength((int) ${input$knockback});
		_ent.world.addEntity(entityToSpawn);
	}
}