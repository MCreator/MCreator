if (world instanceof ServerLevel _level)
	_level.addFreshEntity(new ExperienceOrb(_level, ${input$x}, ${input$y}, ${input$z}, ${opt.toInt(input$xpamount)}));