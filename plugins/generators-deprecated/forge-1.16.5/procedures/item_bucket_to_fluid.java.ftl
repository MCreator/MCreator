<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState toBlock(Item _bckt) {
		if(_bckt instanceof BucketItem) {
		    return ((BucketItem) _bckt).getFluid().getDefaultState().getBlockState();
		}
		return Blocks.AIR.getDefaultState();
	}
}.toBlock(${mappedMCItemToItem(input$source)}))