<#include "mcelements.ftl">
(new Object(){
	public int getHarvestLevel(Block _bl) {
		return TierSortingRegistry.getSortedTiers().stream().filter(t -> t.getTag() != null && _bl.defaultBlockState().is(t.getTag()))
                    .map(Tier::getLevel).findFirst().orElse(0);
    }
}.getHarvestLevel(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock()))