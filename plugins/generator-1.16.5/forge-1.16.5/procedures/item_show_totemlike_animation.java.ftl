<#include "mcitems.ftl">
if(world.isRemote()) {
    Minecraft.getInstance().gameRenderer.displayItemActivation(${mappedMCItemToItemStackCode(input$item, 1)});
}