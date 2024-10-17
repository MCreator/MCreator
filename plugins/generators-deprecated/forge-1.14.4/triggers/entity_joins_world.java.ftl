@SubscribeEvent public void onEntityJoin(EntityJoinWorldEvent event){
		World world=event.getWorld();
		Entity entity=event.getEntity();
		double i=entity.posX;
		double j=entity.posY;
		double k=entity.posZ;
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}