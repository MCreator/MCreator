<#assign plr = "_plr" + cbi>
(${input$entity} instanceof ServerPlayer ${plr} && ${plr}.level() instanceof ServerLevel && ${plr}.getAdvancements()
        .getOrStartProgress(${plr}.server.getAdvancements().get(new ResourceLocation("${generator.map(field$achievement, "achievements")}"))).isDone())