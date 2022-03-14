<#include "mcelements.ftl">
((world instanceof World)?((World) world).getRedstonePower(${toBlockPos(input$x,input$y,input$z)}, ${input$direction}):0)