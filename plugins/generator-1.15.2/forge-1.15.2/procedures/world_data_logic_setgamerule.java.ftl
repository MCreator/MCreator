<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
    world.getWorld().getGameRules().get(${generator.map(field$gamerulesboolean, "gamerules")}).set(${input$gameruleValue},world.getWorld().getServer());
</#if>