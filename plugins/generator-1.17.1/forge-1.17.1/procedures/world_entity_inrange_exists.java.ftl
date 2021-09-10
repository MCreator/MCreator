(!world.getEntitiesOfClass(${generator.map(field$entity, "entities", 0)}.class, new AABB(
            ${input$x} - (${input$range} / 2d), ${input$y} - (${input$range} / 2d), ${input$z} - (${input$range} / 2d),
            ${input$x} + (${input$range} / 2d), ${input$y} + (${input$range} / 2d), ${input$z} + (${input$range} / 2d)), e -> true)
    .isEmpty())