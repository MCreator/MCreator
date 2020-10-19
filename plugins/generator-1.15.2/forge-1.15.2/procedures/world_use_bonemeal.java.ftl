if(BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL),world.getWorld(),new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}))||
    BoneMealItem.growSeagrass(new ItemStack(Items.BONE_MEAL),world.getWorld(),new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),(Direction)null)){
    if(!world.getWorld().isRemote)
    	world.getWorld().playEvent(2005,new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),0);
}