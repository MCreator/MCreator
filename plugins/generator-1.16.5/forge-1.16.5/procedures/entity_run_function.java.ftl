if(!${input$entity}.world.isRemote && ${input$entity}.world.getServer() != null) {
	Optional<FunctionObject> _fopt = ${input$entity}.world.getServer().getFunctionManager().get(new ResourceLocation(${input$function}));
	if(_fopt.isPresent()) {
		FunctionObject _fobj = _fopt.get();
		${input$entity}.world.getServer().getFunctionManager().execute(_fobj, ${input$entity}.getCommandSource());
	}
}