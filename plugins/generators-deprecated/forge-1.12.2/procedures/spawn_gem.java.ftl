<#include "mcitems.ftl">
if(!world.isRemote){
	EntityItem entityToSpawn=new EntityItem(world, ${input$x}, ${input$y}, ${input$z}, ${mappedMCItemToItemStackCode(input$block, 1)});
	entityToSpawn.setPickupDelay(10);
	world.spawnEntity(entityToSpawn);
}