if (world instanceof Level _level && !_level.isClientSide())
	_level.explode(null, ${input$x}, ${input$y}, ${input$z}, ${opt.toFloat(input$power)},
	Explosion.BlockInteraction.${field$mode?replace("BLOCK", "DESTROY")?replace("MOB", "DESTROY")?replace("TNT", "BREAK")});