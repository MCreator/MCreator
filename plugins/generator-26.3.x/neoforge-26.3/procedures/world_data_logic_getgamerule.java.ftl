<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
(world instanceof ServerLevel _serverLevelGR${cbi} && _serverLevelGR${cbi}.getGameRules().get(${generator.map(field$gamerulesboolean, "gamerules")}))
<#else>false</#if>