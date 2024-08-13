<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
world.getLevelData().getGameRules().getRule(${generator.map(field$gamerulesboolean, "gamerules")}).set(${input$gameruleValue}, world.getServer());
</#if>