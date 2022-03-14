<#include "mcelements.ftl">
if(world instanceof World) ${(field$dimension.toString().replace("CUSTOM:", ""))}Dimension.portal.portalSpawn((World) world, ${toBlockPos(input$x,input$y,input$z)});