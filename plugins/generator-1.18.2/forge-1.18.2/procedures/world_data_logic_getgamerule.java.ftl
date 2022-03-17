<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
(world.getLevelData().getGameRules().getBoolean(${generator.map(field$gamerulesboolean, "gamerules")}))
<#else>(false)</#if>