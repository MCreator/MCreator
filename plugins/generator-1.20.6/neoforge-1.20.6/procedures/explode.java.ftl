if (world instanceof Level _level && !_level.isClientSide())
	_level.explode(null, ${input$x}, ${input$y}, ${input$z}, ${opt.toFloat(input$power)}, Level.ExplosionInteraction.${field$mode});