<#include "mcelements.ftl">
/*@int*/(new Object(){
	public int getHarvestLevel(BlockState _bs) {
		return TierSortingRegistry.getSortedTiers().stream().filter(t -> t.getTag() != null && _bs.is(t.getTag()))
				.map(Tier::getLevel).findFirst().orElse(0);
	}
}.getHarvestLevel(world.getBlockState(${toBlockPos(input$x,input$y,input$z)})))