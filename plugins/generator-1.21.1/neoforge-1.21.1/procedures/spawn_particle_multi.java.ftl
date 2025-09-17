<@head>if (world instanceof ServerLevel _level) {</@head>
	_level.sendParticles(${generator.map(field$particle, "particles")}, ${input$x}, ${input$y}, ${input$z}, ${opt.toInt(input$count)}, ${input$dx}, ${input$dy}, ${input$dz}, ${input$speed});
<@tail>}</@tail>