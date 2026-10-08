<#include "mcelements.ftl">
<@addTemplate file="utils/energy/block_energy_can_extract.java.ftl"/>
(canExtractEnergy(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}))