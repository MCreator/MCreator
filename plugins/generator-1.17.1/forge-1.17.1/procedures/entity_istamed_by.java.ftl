(${input$entity} instanceof TamableAnimal _tam_isTamedBy && ${input$tamedBy} instanceof LivingEntity _ent_isOwner
        ? _tam_isTamedBy.isOwnedBy(_ent_isOwner):false)