((Entity) world.getEntitiesOfClass(${generator.map(field$entity, "entities", 0)}.class, new AABB(
            ${input$x} - (${input$range} / 2d), ${input$y} - (${input$range} / 2d), ${input$z} - (${input$range} / 2d),
            ${input$x} + (${input$range} / 2d), ${input$y} + (${input$range} / 2d), ${input$z} + (${input$range} / 2d)), e -> true)
    .stream()
	.sorted(new Object() {
		Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
		    return Comparator.comparing((Function<Entity, Double>) (_entcnd -> _entcnd.distanceToSqr(_x, _y, _z)));
		}
	}.compareDistOf(${input$x}, ${input$y}, ${input$z}))
    .findFirst().orElse(null)
)