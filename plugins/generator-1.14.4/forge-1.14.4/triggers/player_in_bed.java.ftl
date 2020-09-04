@SubscribeEvent public void onPlayerInBed(PlayerSleepInBedEvent event){
		PlayerEntity entity=event.getPlayer();
		int i=event.getPos().getX();
		int j=event.getPos().getY();
		int k=event.getPos().getZ();
		World world=entity.world;
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}