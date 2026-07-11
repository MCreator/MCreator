<#include "mcelements.ftl">
<@addTemplate file="utils/fluidtank/block_fluidtank_get.java.ftl"/>
/*@int*/(getFluidTankLevel(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$tank)}, ${input$direction}))