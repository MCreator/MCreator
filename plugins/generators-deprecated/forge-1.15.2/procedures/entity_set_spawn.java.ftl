if(${input$entity} instanceof PlayerEntity)
	((PlayerEntity)${input$entity}).setSpawnPoint(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), true, false, ${input$entity}.dimension);