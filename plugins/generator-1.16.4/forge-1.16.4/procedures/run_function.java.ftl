if(!((World) world).isRemote && ((World) world).getServer() != null) {
		Optional<FunctionObject> _fopt = ((World) world).getServer().getFunctionManager().get(new ResourceLocation(${input$function}));
		if(_fopt.isPresent()) {
			FunctionObject _fobj = _fopt.get();
			((World) world).getServer().getFunctionManager().execute(_fobj,
				new CommandSource(ICommandSource.DUMMY, new Vector3d(${input$x}, ${input$y}, ${input$z}), Vector2f.ZERO,
					((ServerWorld) world).getWorldInfo(), 4, "", new StringTextComponent(""), ((World) world).getServer(), null));
		}
}