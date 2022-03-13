(new Object(){
	public int getHarvestLevel(Block _bl) {
		return TierSortingRegistry.getSortedTiers().stream().filter(t -> t.getTag() != null && _bl.defaultBlockState().is(t.getTag()))
                    .map(Tier::getLevel).findFirst().orElse(0);
    }
}.getHarvestLevel(world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getBlock()))