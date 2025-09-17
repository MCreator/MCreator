<@head>if (world instanceof ServerLevel _level) {</@head>
	_level.addFreshEntity(new ExperienceOrb(_level, ${input$x}, ${input$y}, ${input$z}, ${opt.toInt(input$xpamount)}));
<@tail>}</@tail>