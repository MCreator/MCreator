<#include "mcelements.ftl">
<@addTemplate file="utils/energy/block_energy_get.java.ftl"/>
/*@int*/(getEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}))