if(!world.isRemote&&entity instanceof EntityLivingBase){
	EntityTippedArrow entityToSpawn=new EntityTippedArrow(world,(EntityLivingBase)entity);
	entityToSpawn.shoot(entity.getLookVec().x,entity.getLookVec().y,entity.getLookVec().z,((float)${input$speed})*2.0F,0);
	entityToSpawn.setDamage(((float)${input$damage})*2.0F);
	entityToSpawn.setKnockbackStrength((int)${input$knockback});
	world.spawnEntity(entityToSpawn);
}