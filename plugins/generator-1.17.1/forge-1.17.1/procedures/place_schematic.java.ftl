if(world instanceof ServerLevel _serverworld) {
	StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("${modid}" ,"${field$schematic}"));
	if(template!=null){
		template.placeInWorld(_serverworld,
				new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),
				new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),
				new StructurePlaceSettings()
						.setRotation(Rotation.${field$rotation!'NONE'})
						.setMirror(Mirror.${field$mirror!'NONE'})
						.setIgnoreEntities(false), _serverworld.random, 3);
	}
}
