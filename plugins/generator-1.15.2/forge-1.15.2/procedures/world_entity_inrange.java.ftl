(world.getEntitiesWithinAABB(${generator.map(field$entity, "entities", 0)}.class, new AxisAlignedBB(
            ${input$x} - (${input$range} / 2d), ${input$y} - (${input$range} / 2d), ${input$z} - (${input$range} / 2d),
            ${input$x} + (${input$range} / 2d), ${input$y} + (${input$range} / 2d), ${input$z} + (${input$range} / 2d)), null)
    .stream()
    .sorted(Comparator.comparing(_entcnd -> _entcnd.getDistanceSq(${input$x}, ${input$y}, ${input$z})))
    .findFirst().orElse(null)
)