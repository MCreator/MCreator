@SubscribeEvent public void onLivingDropXp(LivingExperienceDropEvent event) {
	if (event != null && event.getEntity() != null) {
		Entity entity = event.getEntity();
		int i = (int) entity.posX;
		int j = (int) entity.posY;
		int k = (int) entity.posZ;
		PlayerEntity attacked = event.getAttackingPlayer();
		int droppedxp = (int) event.getDroppedExperience();
		int originalxp = (int) event.getOriginalExperience();
		World world = entity.world;
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("droppedexperience", droppedxp);
		dependencies.put("originalexperience", originalxp);
		dependencies.put("sourceentity", attacked);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}
}