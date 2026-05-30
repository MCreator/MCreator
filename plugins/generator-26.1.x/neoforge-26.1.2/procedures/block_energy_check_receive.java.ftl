<#include "mcelements.ftl">
<@addTemplate file="utils/energy/block_energy_check_receive.java.ftl"/>
/*@int*/(receiveEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$amount)}, ${input$direction}))