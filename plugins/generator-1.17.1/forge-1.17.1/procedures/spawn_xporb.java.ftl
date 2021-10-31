if (world instanceof Level _level && !_level.isClientSide())
	_level.addFreshEntity(new ExperienceOrb(_level, ${input$x}, ${input$y}, ${input$z},${opt.toInt(input$xpamount)}));