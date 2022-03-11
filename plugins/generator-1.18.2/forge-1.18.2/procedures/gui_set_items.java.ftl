<#include "mcitems.ftl">
if(${input$entity} instanceof ServerPlayer _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
	_setstack.setCount(${opt.toInt(input$amount)});
	((Slot) _slots.get(${opt.toInt(input$slotid)})).set(_setstack);
	_player.containerMenu.broadcastChanges();
}