<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
if(world instanceof Level _level)
    _level.getGameRules().getRule(${generator.map(field$gamerulesboolean, "gamerules")}).set(${input$gameruleValue}, _level.getServer());
</#if>