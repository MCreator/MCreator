<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
world.getLevelData().getGameRules().getRule(${generator.map(field$gamerulesnumber, "gamerules")}).set(${opt.toInt(input$gameruleValue)}, world.getServer());
</#if>