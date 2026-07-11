(!world.getEntitiesOfClass(${generator.map(field$entity, "entities", 0)}.class,
	new AABB(Vec3.ZERO, Vec3.ZERO).move(new Vec3(${input$x}, ${input$y}, ${input$z})).inflate(${input$range} / 2d), e -> true)
	.isEmpty())