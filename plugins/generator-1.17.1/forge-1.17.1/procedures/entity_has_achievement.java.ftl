(${input$entity} instanceof ServerPlayer _playerHasAdvnc && _playerHasAdvnc.level instanceof ServerLevel ? _playerHasAdvnc.getAdvancements()
        .getOrStartProgress(_playerHasAdvnc.server.getAdvancements().getAdvancement(new ResourceLocation("${generator.map(field$achievement, "achievements")}"))).isDone():false)