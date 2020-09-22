@SubscribeEvent public void onUseItemStart(LivingEntityUseItemEvent.Stop event) {
	if (event != null && event.getEntity() != null) {
		Entity entity = event.getEntity();
		int i = (int) entity.getPosX();
		int j = (int) entity.getPosY();
		int k = (int) entity.getPosZ();
		double duration = event.getDuration();
    	ItemStack itemstack = event.getItem();
		World world = entity.world;
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("x", i);
		dependencies.put("y", j);
		dependencies.put("z", k);
		dependencies.put("itemstack", itemstack);
    	dependencies.put("duration", duration);
		dependencies.put("world", world);
		dependencies.put("entity", entity);
		dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}
}