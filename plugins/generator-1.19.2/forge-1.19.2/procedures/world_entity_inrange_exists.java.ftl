(!world.getEntitiesOfClass(${generator.map(field$entity, "entities", 0)}.class,
	AABB.ofSize(new Vec3(${input$x}, ${input$y}, ${input$z}), ${input$range}, ${input$range}, ${input$range}), e -> true)
	.isEmpty())