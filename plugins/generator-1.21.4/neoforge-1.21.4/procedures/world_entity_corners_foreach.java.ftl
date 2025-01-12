for (Entity entityiterator : world.getEntities(${input$exclude}, new AABB(${input$x1}, ${input$y1}, ${input$z1}, ${input$x2}, ${input$y2}, ${input$z2}))) {
	${statement$foreach}
}