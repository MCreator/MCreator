<#include "mcitems.ftl">
if(world instanceof Level _level && !_level.isClientSide()) {
	ItemEntity entityToSpawn=new ItemEntity(_level, ${input$x}, ${input$y}, ${input$z}, ${mappedMCItemToItemStackCode(input$block, 1)});
	entityToSpawn.setPickUpDelay(${opt.toInt(input$pickUpDelay!10)});
    <#if (field$despawn!true)?lower_case == "false">
    entityToSpawn.setUnlimitedLifetime();
    </#if>
	_level.addFreshEntity(entityToSpawn);
}