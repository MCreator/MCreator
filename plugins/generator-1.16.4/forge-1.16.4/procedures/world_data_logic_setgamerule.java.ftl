<#if generator.map(field$gamerulesboolean, "gamerules") != "null">
    if(world instanceof World) {
        ((World) world).getGameRules().get(${generator.map(field$gamerulesboolean, "gamerules")}).set(${input$gameruleValue},((World) world).getServer());
    }
</#if>