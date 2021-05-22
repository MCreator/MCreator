@SubscribeEvent public static void onPlayerCriticalHit(CriticalHitEvent event) {
	Entity entity=event.getTarget();
	PlayerEntity sourceentity=event.getPlayer();
	double i=sourceentity.getPosX();
	double j=sourceentity.getPosY();
	double k=sourceentity.getPosZ();
	World world=sourceentity.world;
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x", i);
	dependencies.put("y", j);
	dependencies.put("z", k);
	dependencies.put("world", world);
	dependencies.put("entity", entity);
	dependencies.put("sourceentity", sourceentity);
	dependencies.put("damagemodifier", event.getDamageModifier());
	dependencies.put("isvanillacritical", event.isVanillaCritical());
	dependencies.put("event", event);
	executeProcedure(dependencies);
}