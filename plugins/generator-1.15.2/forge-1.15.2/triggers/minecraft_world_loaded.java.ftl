@SubscribeEvent public void onWorldLoad(WorldEvent.Load event){
		World world=event.getWorld().getWorld();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("world",world);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}