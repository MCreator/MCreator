if(!world.getWorld().isRemote) {
	Template template = ((ServerWorld)world.getWorld()).getSaveHandler().getStructureTemplateManager()
		.getTemplateDefaulted(new ResourceLocation("${modid}", "${field$schematic}"));

	if (template != null) {
		template.addBlocksToWorld(world,
			new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}),
				new PlacementSettings()
						.setRotation(Rotation.${field$rotation!'NONE'})
						.setMirror(Mirror.${field$mirror!'NONE'})
						.setChunk(null)
						.setIgnoreEntities(false));
	}
}
