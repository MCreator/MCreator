if(!world.getWorld().isRemote) {
	world.playSound(null, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}),
    	(net.minecraft.util.SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
		SoundCategory.${field$category!"NEUTRAL"}, (float) ${input$level}, (float) ${input$pitch});
} else {
	world.getWorld().playSound(${input$x}, ${input$y}, ${input$z},
    	(net.minecraft.util.SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
		SoundCategory.${field$category!"NEUTRAL"}, (float) ${input$level}, (float) ${input$pitch}, false);
}