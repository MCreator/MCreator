<#include "mcelements.ftl">
<@addTemplate file="utils/energy/block_energy_check_extract.java.ftl"/>
(extractEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)}, ${opt.toInt(input$amount)}, ${input$direction}))