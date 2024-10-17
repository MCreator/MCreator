<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
    (world.getWorldInfo().getGameRulesInstance().getInt(${generator.map(field$gamerulesnumber, "gamerules")}))
<#else>
    (0)
</#if>