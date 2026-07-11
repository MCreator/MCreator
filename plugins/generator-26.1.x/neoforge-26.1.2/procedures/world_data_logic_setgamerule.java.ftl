<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
if (world instanceof ServerLevel _serverLevel)
	_serverLevel.getGameRules().set(${generator.map(field$gamerulesboolean, "gamerules")}, ${input$gameruleValue}, world.getServer());
</#if>