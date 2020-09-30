<#include "mcitems.ftl">
if(world instanceof World && !world.getWorld().isRemote) {
	ItemEntity entityToSpawn=new ItemEntity(world.getWorld(), ${input$x}, ${input$y}, ${input$z}, ${mappedMCItemToItemStackCode(input$block, 1)});
	entityToSpawn.setPickupDelay(${input$pickUpDelay});
    <#if field$noDespawn?lower_case == "false">
        entityToSpawn.setNoDespawn();
    </#if>
	world.addEntity(entityToSpawn);
}