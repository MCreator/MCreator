world.playSound((EntityPlayer)null, ${input$x}, ${input$y}, ${input$z},
        (net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY.getObject(
        		new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
		SoundCategory.NEUTRAL,(float)${input$level},(float)${input$pitch});