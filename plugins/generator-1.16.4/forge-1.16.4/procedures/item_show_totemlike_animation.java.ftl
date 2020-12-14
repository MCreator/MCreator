<#include "mcitems.ftl">
if(world instanceof World && !world.isRemote()) {
    Minecraft.getInstance().gameRenderer.displayItemActivation(${mappedMCItemToItemStackCode(input$item, 1)});
}