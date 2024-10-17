@SubscribeEvent public void onExplode(ExplosionEvent.Detonate event){
		World world = event.getWorld();
		Explosion explosion = event.getExplosion();
		int i=(int)explosion.getPosition().x;
		int j=(int)explosion.getPosition().y;
		int k=(int)explosion.getPosition().z;
		java.util.HashMap<String, Object> dependencies=new java.util.HashMap<>();
		dependencies.put("x",i);
		dependencies.put("y",j);
		dependencies.put("z",k);
		dependencies.put("world",world);
		dependencies.put("event",event);
		this.executeProcedure(dependencies);
		}