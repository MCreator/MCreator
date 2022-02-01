{
	final Vec3 _center = new Vec3(${input$x}, ${input$y}, ${input$z});
	List<Entity> _entfound = world.getEntitiesOfClass(Entity.class,
		AABB.ofSize(_center, ${input$range}, ${input$range}, ${input$range}), e -> true).stream()
		.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
	for (Entity entityiterator : _entfound) {
		${statement$foreach}
	}
}