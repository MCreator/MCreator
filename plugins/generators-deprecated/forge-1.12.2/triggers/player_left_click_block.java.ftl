@SubscribeEvent public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
		EntityPlayer entity=event.getEntityPlayer();
		int i=event.getPos().getX();
		int j=event.getPos().getY();
		int k=event.getPos().getZ();
		World world=event.getWorld();
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x" ,i);
		dependencies.put("y" ,j);
		dependencies.put("z" ,k);
		dependencies.put("world" ,world);
		dependencies.put("entity" ,entity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
}