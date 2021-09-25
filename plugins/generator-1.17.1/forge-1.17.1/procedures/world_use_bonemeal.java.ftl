if(world instanceof Level _level) {
    if(BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL),_level,new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}))||
        BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL),_level,new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),null)){
        if(!_level.isClientSide())
    	    _level.levelEvent(2005,new BlockPos((int) ${input$x},(int) ${input$y},(int) ${input$z}),0);
    }
}