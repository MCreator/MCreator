<#include "mcelements.ftl">
<@addTemplate file="utils/energy/block_energy_get_max.java.ftl"/>
(getMaxEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}))