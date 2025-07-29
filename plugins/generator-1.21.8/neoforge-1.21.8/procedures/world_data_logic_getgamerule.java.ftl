<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
(world instanceof ServerLevel _serverLevelGR${cbi} && _serverLevelGR${cbi}.getGameRules().getBoolean(${generator.map(field$gamerulesboolean, "gamerules")}))
<#else>false</#if>