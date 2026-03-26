/*@int*/<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
(world instanceof ServerLevel _serverLevelGR${cbi} ? _serverLevelGR${cbi}.getGameRules().getInt(${generator.map(field$gamerulesnumber, "gamerules")}) : 0)
<#else>0</#if>