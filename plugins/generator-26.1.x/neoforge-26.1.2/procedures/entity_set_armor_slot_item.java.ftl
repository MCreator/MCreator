<#include "mcitems.ftl">
<#include "mcelements.ftl">
if (${input$entity} instanceof LivingEntity _living) {
	_living.setItemSlot(${toArmorSlot(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)});
}