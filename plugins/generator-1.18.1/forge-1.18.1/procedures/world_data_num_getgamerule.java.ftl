/*@int*/<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
(world.getLevelData().getGameRules().getInt(${generator.map(field$gamerulesnumber, "gamerules")}))
<#else>0</#if>