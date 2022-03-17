if (world instanceof Level _level) {
    BlockPos _bp = new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z});
    if (BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), _level, _bp)
        || BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL), _level, _bp, null)){
        if (!_level.isClientSide())
    	    _level.levelEvent(2005, _bp, 0);
    }
}