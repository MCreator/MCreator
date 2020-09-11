@SubscribeEvent public void onEntityAttacked(LivingAttackEvent event){
	if(event!=null&&event.getEntity()!=null){
		Entity entity=event.getEntity();
		Entity sourceentity=event.getSource().getTrueSource();
		Entity projectileentity=event.getSource().getImmediateSource();
		double i=entity.getPosX();
		double j=entity.getPosY();
		double k=entity.getPosZ();
		double amount = event.getAmount();
		World world=entity.world;
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("amount", amount);
		dependencies.put("world",world);
		dependencies.put("entity",entity);
		dependencies.put("sourceentity",sourceentity);
		dependencies.put("projectileentity",projectileentity);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
	}
}