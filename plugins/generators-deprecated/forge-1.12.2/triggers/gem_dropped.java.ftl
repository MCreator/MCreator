@SubscribeEvent public void onGemDropped(ItemTossEvent event){
		EntityPlayer entity=event.getPlayer();
		int i=(int)entity.posX;
		int j=(int)entity.posY;
		int k=(int)entity.posZ;
		World world=entity.world;
		ItemStack itemstack=event.getEntityItem().getItem();
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("itemstack",itemstack);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}