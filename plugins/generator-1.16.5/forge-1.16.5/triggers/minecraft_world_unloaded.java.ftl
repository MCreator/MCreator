@SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) {
	IWorld world=event.getWorld();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("world",world);
	dependencies.put("event",event);
	executeProcedure(dependencies);
}