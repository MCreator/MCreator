if(!world.getWorld().isRemote) {
	Template template = ((ServerWorld)world.getWorld()).getSaveHandler().getStructureTemplateManager()
		.getTemplateDefaulted(new ResourceLocation("${modid}", "${field$schematic}"));

	if (template != null) {
		template.addBlocksToWorld(world,
			new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}),
				new PlacementSettings()
						.setRotation(Rotation.${field$rotation})
						.setMirror(Mirror.${field$mirror})
						.setChunk(null)
						.setIgnoreEntities(false));
	}
}
