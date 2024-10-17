((${input$entity} instanceof TameableEntity && ${input$tamedBy} instanceof LivingEntity)
        ?((TameableEntity)${input$entity}).isOwner((LivingEntity)${input$tamedBy}):false)