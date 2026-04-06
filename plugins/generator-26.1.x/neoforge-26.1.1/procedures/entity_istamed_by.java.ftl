(${input$entity} instanceof TamableAnimal _tamIsTamedBy${cbi} && ${input$tamedBy} instanceof LivingEntity _livEnt${cbi}
        && _tamIsTamedBy${cbi}.isOwnedBy(_livEnt${cbi}))