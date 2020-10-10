{
	if (BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), (World) world, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z})) ||
	BoneMealItem.growSeagrass(new ItemStack(Items.BONE_MEAL), (World) world, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}), null)) {
		if (!((World) world).isRemote)
	((World) world).playEvent(2005, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}), 0);
	}
}
