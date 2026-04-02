<#assign plr = "_plr" + cbi>
(${input$entity} instanceof ServerPlayer ${plr} && ${plr}.level() instanceof ServerLevel _serverLevel${cbi} && ${plr}.getAdvancements()
        .getOrStartProgress(_serverLevel${cbi}.getServer().getAdvancements().get(Identifier.parse("${generator.map(field$achievement, "achievements")}"))).isDone())