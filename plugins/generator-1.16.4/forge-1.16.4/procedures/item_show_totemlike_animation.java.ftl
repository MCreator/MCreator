<#include "mcitems.ftl">
if(world instanceof World && !((World) world).isRemote) {
    Minecraft.getInstance().gameRenderer.displayItemActivation(${mappedMCItemToItemStackCode(input$item, 1)});
}