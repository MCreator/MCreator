<#include "mcelements.ftl">
<@addTemplate file="utils/energy/block_energy_can_receive.java.ftl"/>
(canReceiveEnergy(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}))