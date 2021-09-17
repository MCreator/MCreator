<#include "mcitems.ftl">
if (${input$entity} instanceof LivingEntity _entity) {
	if (_entity instanceof Player _playerEntity)
		_playerEntity.getInventory().armor.set((int) ${input$slotid}, ${mappedMCItemToItemStackCode(input$item, 1)});
	else
		_entity.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, (int) ${input$slotid}), ${mappedMCItemToItemStackCode(input$item, 1)});
	if (_entity instanceof ServerPlayer _serverPlayer) _serverPlayer.getInventory().setChanged();
}