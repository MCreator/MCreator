if(!world.getWorld().isRemote && world.getWorld().getServer() != null) {
		Optional<FunctionObject> _fopt = world.getWorld().getServer().getFunctionManager().get(new ResourceLocation(${input$function}));
		if(_fopt.isPresent()) {
			FunctionObject _fobj = _fopt.get();
			world.getWorld().getServer().getFunctionManager().execute(_fobj,
				new CommandSource(ICommandSource.DUMMY, new Vec3d(${input$x}, ${input$y}, ${input$z}), Vec2f.ZERO,
					(ServerWorld) world.getWorld(), 4, "", new StringTextComponent(""), world.getWorld().getServer(), null));
		}
}