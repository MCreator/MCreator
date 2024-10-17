@SubscribeEvent public void onItemExpire(ItemExpireEvent event){
		Entity entity=event.getEntity();
		double i=entity.posX;
		double j=entity.posY;
		double k=entity.posZ;
		ItemStack itemstack=event.getEntityItem().getItem();
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",entity.world);
		dependencies.put("entity",entity);
		dependencies.put("event",event);
		dependencies.put("itemstack",itemstack);
		this.executeProcedure(dependencies);
		}