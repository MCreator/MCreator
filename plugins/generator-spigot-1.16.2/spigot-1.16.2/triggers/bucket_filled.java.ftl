@EventHandler public void onPlayerFillBucket(PlayerBucketFillEvent event) {
	World world=event.getPlayer().getWorld();
	Player player=event.getPlayer();
	ItemStack itemstack = event.getBucket();
	double i=player.getLocation().getX();
	double j=player.getLocation().getY();
	double k=player.getLocation().getZ();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("player",player);
	dependencies.put("itemstack",itemstack);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}
