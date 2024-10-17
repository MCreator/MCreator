<#include "mcitems.ftl">
if (world.getWorld().isRemote) {
    Minecraft.getInstance().gameRenderer.displayItemActivation(${mappedMCItemToItemStackCode(input$item, 1)});
}