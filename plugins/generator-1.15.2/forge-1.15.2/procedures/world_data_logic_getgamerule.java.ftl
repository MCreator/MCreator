<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
(world.getWorld().getGameRules().getBoolean(${generator.map(field$gamerulesboolean, "gamerules")}))
<#else>
(false)
</#if>