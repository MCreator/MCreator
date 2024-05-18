<#include "mcitems.ftl">
if (world instanceof ServerLevel _level) {
	ItemEntity entityToSpawn = new ItemEntity(_level, ${input$x}, ${input$y}, ${input$z}, ${mappedMCItemToItemStackCode(input$block, 1)});
	entityToSpawn.setPickUpDelay(${opt.toInt(input$pickUpDelay!10)});
	<#if (field$despawn!"TRUE") == "FALSE">
	entityToSpawn.setUnlimitedLifetime();
	</#if>
	_level.addFreshEntity(entityToSpawn);
}