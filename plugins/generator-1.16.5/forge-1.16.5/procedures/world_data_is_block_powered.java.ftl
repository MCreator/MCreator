<#include "mcelements.ftl">
((world instanceof World)?((World) world).isBlockPowered(${toBlockPos(input$x,input$y,input$z)}):false)