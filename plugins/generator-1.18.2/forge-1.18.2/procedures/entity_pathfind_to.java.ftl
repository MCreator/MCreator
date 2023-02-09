if (${input$entity} instanceof PathfinderMob _entity)
	_entity.getNavigation().moveTo(${input$x}, ${input$y}, ${input$z}, ${field$speed});