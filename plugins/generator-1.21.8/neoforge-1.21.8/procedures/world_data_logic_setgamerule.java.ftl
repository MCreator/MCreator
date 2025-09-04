<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
if (world instanceof ServerLevel _serverLevel)
	_serverLevel.getGameRules().getRule(${generator.map(field$gamerulesboolean, "gamerules")}).set(${input$gameruleValue}, world.getServer());
</#if>