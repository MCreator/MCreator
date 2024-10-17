<#include "mcitems.ftl">
{
	TileEntity inv=world.getTileEntity(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	if(inv!=null&&(inv instanceof TileEntityLockableLoot)) {
		ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
		_setstack.setCount(${input$amount});
		((TileEntityLockableLoot)inv).setInventorySlotContents((int)(${input$slotid}), _setstack);
	}
}