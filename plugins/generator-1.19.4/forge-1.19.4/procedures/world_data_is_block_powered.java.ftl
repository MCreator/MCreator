<#include "mcelements.ftl">
(world instanceof Level _level${customBlockIndex} && _level${customBlockIndex}.hasNeighborSignal(${toBlockPos(input$x,input$y,input$z)}))