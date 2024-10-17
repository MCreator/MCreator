@SubscribeEvent public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event){
	Entity entity = event.player;
	java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
	dependencies.put("x",(int)entity.posX);
	dependencies.put("y",(int)entity.posY);
	dependencies.put("z",(int)entity.posZ);
	dependencies.put("world",entity.world);
	dependencies.put("entity",entity);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}