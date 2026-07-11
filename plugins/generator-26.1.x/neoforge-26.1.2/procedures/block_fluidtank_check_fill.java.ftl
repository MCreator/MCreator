<#include "mcelements.ftl">
<@addTemplate file="utils/fluidtank/block_fluidtank_check_fill.java.ftl"/>
/*@int*/(fillTankSimulate(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$amount)}, ${input$direction}, ${generator.map(field$fluid, "fluids")}))