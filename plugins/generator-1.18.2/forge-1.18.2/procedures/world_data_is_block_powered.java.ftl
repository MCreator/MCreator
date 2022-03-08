<#include "mcelements.ftl">
(world instanceof Level _lvl_isPow ? _lvl_isPow.hasNeighborSignal(${toBlockPos(input$x,input$y,input$z)}):false)