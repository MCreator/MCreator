if(world instanceof ServerWorld && ((ServerWorld) world).getServer() != null) {
		Optional<FunctionObject> _fopt = ((ServerWorld) world).getServer().getFunctionManager().get(new ResourceLocation(${input$function}));
		if(_fopt.isPresent()) {
			FunctionObject _fobj = _fopt.get();
			((ServerWorld) world).getServer().getFunctionManager().execute(_fobj,
				new CommandSource(ICommandSource.DUMMY, new Vector3d(${input$x}, ${input$y}, ${input$z}), Vector2f.ZERO,
					(ServerWorld) world, 4, "", new StringTextComponent(""), ((ServerWorld) world).getServer(), null));
		}
}