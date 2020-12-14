<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
    ((world instanceof World)?((World) world).getGameRules().getInt(${generator.map(field$gamerulesnumber, "gamerules")}):0)
<#else>
    (0)
</#if>