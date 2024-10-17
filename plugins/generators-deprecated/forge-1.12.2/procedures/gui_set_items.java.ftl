<#include "mcitems.ftl">
if(entity instanceof EntityPlayerMP) {
	Container _current = ((EntityPlayerMP) entity).openContainer;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
			_setstack.setCount(${input$amount});
			((Slot) ((Map) invobj).get((int)(${input$slotid}))).putStack(_setstack);
			_current.detectAndSendChanges();
		}
	}
}