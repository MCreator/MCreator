if ((${input$entity} instanceof TameableEntity) && (${input$sourceentity} instanceof PlayerEntity)) {
	((TameableEntity) ${input$entity}).setTamed(true);
	((TameableEntity) ${input$entity}).setTamedBy((PlayerEntity) ${input$sourceentity});
}