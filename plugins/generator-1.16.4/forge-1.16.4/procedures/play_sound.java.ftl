if(world instanceof World && !world.isRemote()) {
	((World) world).playSound(null, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}),
    	(net.minecraft.util.SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
		SoundCategory.NEUTRAL, (float) ${input$level}, (float) ${input$pitch});
} else {
	((World) world).playSound(${input$x}, ${input$y}, ${input$z},
    	(net.minecraft.util.SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
		SoundCategory.NEUTRAL, (float) ${input$level}, (float) ${input$pitch}, false);
}