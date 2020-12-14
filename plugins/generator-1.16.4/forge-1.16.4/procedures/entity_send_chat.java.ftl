if(${input$entity} instanceof PlayerEntity && !${input$entity}.world.isRemote()){
	((PlayerEntity)${input$entity}).sendStatusMessage(new StringTextComponent(${input$text}), ${input$actbar});
}