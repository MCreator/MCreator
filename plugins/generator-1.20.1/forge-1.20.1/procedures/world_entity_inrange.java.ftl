((Entity) world.getEntitiesOfClass(${generator.map(field$entity, "entities", 0)}.class,
	AABB.ofSize(new Vec3(${input$x}, ${input$y}, ${input$z}), ${input$range}, ${input$range}, ${input$range}), e -> true)
	.stream().sorted(new Object() {
		Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
			return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
		}
	}.compareDistOf(${input$x}, ${input$y}, ${input$z})).findFirst().orElse(null))