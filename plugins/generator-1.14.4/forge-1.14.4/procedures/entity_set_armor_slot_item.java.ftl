<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity) {
	((PlayerEntity)${input$entity}).inventory.armorInventory.set((int) ${input$slotid}, ${mappedMCItemToItemStackCode(input$item, 1)});
	if(${input$entity} instanceof ServerPlayerEntity)
		((ServerPlayerEntity)${input$entity}).inventory.markDirty();
}