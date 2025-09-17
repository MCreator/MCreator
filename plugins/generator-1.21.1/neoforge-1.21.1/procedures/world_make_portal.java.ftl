<#include "mcelements.ftl">
<@head>if (world instanceof Level _level) {</@head>
	${field$dimension.replace("CUSTOM:", "")}PortalBlock.portalSpawn(_level, ${toBlockPos(input$x,input$y,input$z)});
<@tail>}</@tail>