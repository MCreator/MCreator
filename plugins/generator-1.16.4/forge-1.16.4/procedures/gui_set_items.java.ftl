<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity) {
	Container _current = ((PlayerEntity) ${input$entity}).openContainer;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
			_setstack.setCount((int) ${input$amount});
			((Slot) ((Map) invobj).get((int)(${input$slotid}))).putStack(_setstack);
			_current.detectAndSendChanges();
		}
	}
}