<#include "mcitems.ftl">
if(world instanceof World && !world.isRemote()) {
	ItemEntity entityToSpawn=new ItemEntity((World) world, ${input$x}, ${input$y}, ${input$z}, ${mappedMCItemToItemStackCode(input$block, 1)});
	entityToSpawn.setPickupDelay((int)${input$pickUpDelay!10});
    <#if (field$despawn!true)?lower_case == "false">
        entityToSpawn.setNoDespawn();
    </#if>
	world.addEntity(entityToSpawn);
}