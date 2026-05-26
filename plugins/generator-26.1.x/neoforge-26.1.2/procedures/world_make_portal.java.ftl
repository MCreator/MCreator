<#include "mcelements.ftl">
if (world instanceof Level _level)
	${field$dimension.replace("CUSTOM:", "")}PortalBlock.portalSpawn(_level, ${toBlockPos(input$x,input$y,input$z)});