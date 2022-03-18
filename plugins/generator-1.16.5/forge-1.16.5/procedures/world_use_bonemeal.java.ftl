<#include "mcelements.ftl">
if(world instanceof World) {
    if(BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL),(World) world,${toBlockPos(input$x,input$y,input$z)})||
        BoneMealItem.growSeagrass(new ItemStack(Items.BONE_MEAL),(World) world,${toBlockPos(input$x,input$y,input$z)},(Direction)null)){
        if(!world.isRemote())
    	    ((World) world).playEvent(2005,${toBlockPos(input$x,input$y,input$z)},0);
    }
}