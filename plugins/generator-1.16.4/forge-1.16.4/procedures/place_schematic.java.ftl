if(world instanceof World && !((World) world).isRemote) {
	Template template=((ServerWorld) world).getStructureTemplateManager()
		.getTemplateDefaulted(new ResourceLocation("${modid}" ,"${field$schematic}"));

	if(template!=null){
		template.func_237144_a_((ServerWorld) world,
			new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),
				new PlacementSettings()
						.setRotation(Rotation.${field$rotation!'NONE'})
						.setMirror(Mirror.${field$mirror!'NONE'})
						.setChunk(null)
						.setIgnoreEntities(false), ((World) world).rand);
	}
}
