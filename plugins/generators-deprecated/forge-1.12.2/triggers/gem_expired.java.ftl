@SubscribeEvent public void onItemExpire(ItemExpireEvent event){
		Entity entity=event.getEntity();
		int i=(int)entity.posX;
		int j=(int)entity.posY;
		int k=(int)entity.posZ;
		ItemStack itemstack=event.getEntityItem().getItem();
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",entity.world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		dependencies.put("itemstack",itemstack);
		this.executeProcedure(dependencies);
		}