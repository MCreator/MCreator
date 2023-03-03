{
	final Vec3 _center${customBlockIndex} = new Vec3(${input$x}, ${input$y}, ${input$z});
	List<Entity> _entfound${customBlockIndex} = world.getEntitiesOfClass(Entity.class,
			new AABB(_center${customBlockIndex}, _center${customBlockIndex}).inflate(${input$range} / 2d), e -> true).stream()
		.sorted(Comparator.comparingDouble(_entcnd${customBlockIndex} -> _entcnd${customBlockIndex}.distanceToSqr(_center${customBlockIndex}))).toList();
	for (Entity entityiterator : _entfound${customBlockIndex}) {
		${statement$foreach}
	}
}
