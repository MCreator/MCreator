if(world instanceof Level _level)
	_level.playSound(_level.isClientSide() ? Minecraft.getInstance().player : null, ${input$x}, ${input$y}, ${input$z},
    	ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
		SoundSource.${generator.map(field$soundcategory!"neutral", "soundcategories")}, ${opt.toFloat(input$level)}, ${opt.toFloat(input$pitch)});