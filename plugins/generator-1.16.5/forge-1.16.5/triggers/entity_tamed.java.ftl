@SubscribeEvent public void onEntityTamed(AnimalTameEvent event) {
    Entity entity = event.getAnimal();
    Entity sourceentity = event.getTamer();
    double i = entity.getPosX();
    double j = entity.getPosY();
    double k = entity.getPosZ();
    World world = entity.world;
    Map<String, Object> dependencies = new HashMap<>();
    dependencies.put("x", i);
    dependencies.put("y", j);
    dependencies.put("z", k);
    dependencies.put("world", world);
    dependencies.put("entity", entity);
    dependencies.put("sourceentity", sourceentity);
    dependencies.put("event", event);
    this.executeProcedure(dependencies);
}