@SubscribeEvent public void onChat(ServerChatEvent event){
		EntityPlayer entity=event.getPlayer();
		int i=(int)entity.posX;
		int j=(int)entity.posY;
		int k=(int)entity.posZ;
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",entity.world);
		dependencies.put("entity",entity);
		dependencies.put("text",event.getMessage());
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}