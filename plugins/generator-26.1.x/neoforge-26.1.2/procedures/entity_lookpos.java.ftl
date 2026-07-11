(Vec3.atLowerCornerOf(${input$entity}.level().clip(new ClipContext(${input$entity}.getEyePosition(1f),${input$entity}.getEyePosition(1f)
	.add(${input$entity}.getViewVector(1f).scale(${input$maxdistance})), ClipContext.Block.${field$block_mode!"OUTLINE"},
  	ClipContext.Fluid.${field$fluid_mode!"NONE"}, ${input$entity})).getBlockPos()))