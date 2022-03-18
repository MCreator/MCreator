(${input$entity} instanceof TamableAnimal _tamIsTamedBy && ${input$tamedBy} instanceof LivingEntity _livEnt
        ? _tamIsTamedBy.isOwnedBy(_livEnt):false)