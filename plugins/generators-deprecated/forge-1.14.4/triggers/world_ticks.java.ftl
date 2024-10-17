@SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event){
		if(event.phase==TickEvent.Phase.END){
		World world=event.world;
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("world",world);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}
		}