<#include "mcitems.ftl">
(new Object(){
	public boolean blockEquals(IBlockState a,IBlockState b){
		try{
		return(a.getBlock()==b.getBlock())&&(a.getBlock().getMetaFromState(a)==b.getBlock().getMetaFromState(b));
		}catch(Exception e){
		return(a.getBlock()==b.getBlock());
		}
		}
		}.blockEquals(${mappedBlockToBlockStateCode(input$a)}, ${mappedBlockToBlockStateCode(input$b)}))