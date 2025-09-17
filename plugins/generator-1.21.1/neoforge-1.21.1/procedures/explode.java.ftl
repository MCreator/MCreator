<@head>if (world instanceof Level _level && !_level.isClientSide()) {</@head>
	_level.explode(null, ${input$x}, ${input$y}, ${input$z}, ${opt.toFloat(input$power)}, Level.ExplosionInteraction.${field$mode});
<@tail>}</@tail>