@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityAttacked(LivingAttackEvent event) {
		if (event!=null && event.getEntity()!=null) {
			Entity entity=event.getEntity();
			Entity sourceentity=event.getSource().getEntity();
			Entity imediatesourceentity=event.getSource().getDirectEntity();
			double i=entity.getX();
			double j=entity.getY();
			double k=entity.getZ();
			double amount = event.getAmount();
			LevelAccessor world = entity.level;
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("x",i);
			dependencies.put("y",j);
			dependencies.put("z",k);
			dependencies.put("amount", amount);
			dependencies.put("world",world);
			dependencies.put("entity",entity);
			dependencies.put("sourceentity",sourceentity);
			dependencies.put("imediatesourceentity",imediatesourceentity);
			dependencies.put("event",event);
			execute(dependencies);
		}
	}