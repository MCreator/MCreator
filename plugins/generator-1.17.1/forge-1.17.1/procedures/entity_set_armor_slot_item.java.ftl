<#include "mcitems.ftl">
<#include "mcelements.ftl">
if (${input$entity} instanceof LivingEntity _entity) {
	if (_entity instanceof Player _player)
		_player.getInventory().armor.set(${opt.toInt(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)});
	else
		_entity.setItemSlot(${toArmorSlot(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)});
	if (_entity instanceof ServerPlayer _serverPlayer) _serverPlayer.getInventory().setChanged();
}