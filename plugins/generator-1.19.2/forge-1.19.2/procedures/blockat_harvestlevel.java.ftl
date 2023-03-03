<#include "mcelements.ftl">
(new Object(){
	public int getHarvestLevel(BlockState _bs) {
		return TierSortingRegistry.getSortedTiers().stream().filter(_t -> _t.getTag() != null && _bs.is(_t.getTag()))
					.map(Tier::getLevel).findFirst().orElse(0);
	}
}.getHarvestLevel(world.getBlockState(${toBlockPos(input$x,input$y,input$z)})))