if(!world.isRemote) {
	Template template=((WorldServer)world).getStructureTemplateManager()
	        .getTemplate(world.getMinecraftServer(),new ResourceLocation("${modid}" ,"${field$schematic}"));
	if(template != null) {
		BlockPos spawnTo=new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
		IBlockState iblockstate=world.getBlockState(spawnTo);
		world.notifyBlockUpdate(spawnTo,iblockstate,iblockstate,3);
		template.addBlocksToWorldChunk(world,spawnTo,new PlacementSettings()
				.setRotation(Rotation.NONE).setMirror(Mirror.NONE).setChunk((ChunkPos)null).setReplacedBlock((Block)null)
				.setIgnoreStructureBlock(false).setIgnoreEntities(false));
	}
}