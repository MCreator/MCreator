<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
if(world instanceof Level _level)
    _level.getGameRules().getRule(${generator.map(field$gamerulesnumber, "gamerules")}).set(${opt.toInt(input$gameruleValue)}, _level.getServer());
</#if>