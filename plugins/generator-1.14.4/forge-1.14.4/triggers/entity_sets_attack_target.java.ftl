@SubscribeEvent public void onEntitySetsAttackTarget(LivingSetAttackTargetEvent event) {
	LivingEntity entity=event.getTarget();
	LivingEntity sourceentity=event.getEntityLiving();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x", sourceentity.posX);
	dependencies.put("y", sourceentity.posY);
	dependencies.put("z", sourceentity.posZ);
	dependencies.put("world", sourceentity.getEntityWorld());
	dependencies.put("entity", entity);
	dependencies.put("sourceentity", sourceentity);
	dependencies.put("event", event);
	this.executeProcedure(dependencies);
}