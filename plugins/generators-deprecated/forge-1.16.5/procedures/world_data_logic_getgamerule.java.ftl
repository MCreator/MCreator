<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
    (world.getWorldInfo().getGameRulesInstance().getBoolean(${generator.map(field$gamerulesboolean, "gamerules")}))
<#else>
    (false)
</#if>