<#include "mcitems.ftl">
if(${input$entity} instanceof ServerPlayer _player) {
	AbstractContainerMenu _current = _player.containerMenu;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
			_setstack.setCount((int) ${input$amount});
			((Slot) ((Map) invobj).get((int)(${input$slotid}))).set(_setstack);
			_current.broadcastChanges();
		}
	}
}