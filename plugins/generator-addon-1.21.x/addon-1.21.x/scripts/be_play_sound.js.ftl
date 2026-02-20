dimension.playSound("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}", { x: ${input$x}, y: ${input$y}, z: ${input$z} },
	{ volume: ${input$level}, pitch: ${input$pitch} } );