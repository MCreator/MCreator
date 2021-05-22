@SubscribeEvent public static void onExplode(ExplosionEvent.Detonate event) {
	World world = event.getWorld();
	Explosion explosion = event.getExplosion();
	double i=explosion.getPosition().x;
	double j=explosion.getPosition().y;
	double k=explosion.getPosition().z;
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("x",i);
	dependencies.put("y",j);
	dependencies.put("z",k);
	dependencies.put("world",world);
	dependencies.put("event",event);
	executeProcedure(dependencies);
}