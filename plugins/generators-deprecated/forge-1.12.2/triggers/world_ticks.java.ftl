@SubscribeEvent public void onWorldTick(TickEvent.WorldTickEvent event){
		if(event.phase==TickEvent.Phase.END){
		World world=event.world;
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("world",world);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}
		}