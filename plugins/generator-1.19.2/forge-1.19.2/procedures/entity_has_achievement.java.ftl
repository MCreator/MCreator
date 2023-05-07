<#assign plr = "_plr" + customBlockIndex>
(${input$entity} instanceof ServerPlayer ${plr} && ${plr}.level instanceof ServerLevel && ${plr}.getAdvancements()
        .getOrStartProgress(${plr}.server.getAdvancements().getAdvancement(new ResourceLocation("${generator.map(field$achievement, "achievements")}"))).isDone())