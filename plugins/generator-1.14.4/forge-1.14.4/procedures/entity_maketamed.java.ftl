if ((${input$entity} instanceof TameableEntity)) {
	((TameableEntity) ${input$entity}).setTamed(true);
	((TameableEntity) ${input$entity}).setTamedBy((PlayerEntity) ${input$sourceentity});
}