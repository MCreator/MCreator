<#include "mcelements.ftl">
/*@int*/(world instanceof Level _lvl_getRedPow ? _lvl_getRedPow.getDirectSignal(${toBlockPos(input$x,input$y,input$z)}, ${input$direction}):0)