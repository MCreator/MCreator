<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
if (world instanceof ServerLevel _serverLevel)
	_serverLevel.getGameRules().getRule(${generator.map(field$gamerulesnumber, "gamerules")}).set(${opt.toInt(input$gameruleValue)}, world.getServer());
</#if>