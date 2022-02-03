{
	final Vec3 _center = new Vec3(${input$x}, ${input$y}, ${input$z});
	List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(${input$range} / 2d), e -> true)
		.stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
	for (Entity entityiterator : _entfound) {
		${statement$foreach}
	}
}
