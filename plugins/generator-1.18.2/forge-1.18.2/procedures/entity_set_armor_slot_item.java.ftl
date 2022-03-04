<#include "mcitems.ftl">
<#include "mcelements.ftl">
{
	Entity _entity = ${input$entity};
	if (_entity instanceof Player _player) {
		_player.getInventory().armor.set(${opt.toInt(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)});
		_player.getInventory().setChanged();
	} else if (_entity instanceof LivingEntity _living) {
		_living.setItemSlot(${toArmorSlot(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)});
	}
}