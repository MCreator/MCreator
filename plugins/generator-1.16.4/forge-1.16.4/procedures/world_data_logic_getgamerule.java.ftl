<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
((world instanceof World)?((World) world).getGameRules().getBoolean(${generator.map(field$gamerulesboolean, "gamerules")}):false)
<#else>
(false)
</#if>