<#include "mcelements.ftl">
<@addTemplate file="utils/fluidtank/block_fluidtank_get_max.java.ftl"/>
/*@int*/(getFluidTankCapacity(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$tank)}, ${input$direction}))