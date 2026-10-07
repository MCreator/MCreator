<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
if (world instanceof ServerLevel _serverLevel)
	_serverLevel.getGameRules().set(${generator.map(field$gamerulesnumber, "gamerules")}, ${opt.toInt(input$gameruleValue)}, world.getServer());
</#if>