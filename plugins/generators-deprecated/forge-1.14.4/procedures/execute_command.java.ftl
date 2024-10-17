if(!world.getWorld().isRemote && world.getWorld().getServer() != null) {
		world.getWorld().getServer().getCommandManager().handleCommand(
			new CommandSource(ICommandSource.field_213139_a_, new Vec3d(${input$x}, ${input$y}, ${input$z}), Vec2f.ZERO,
				(ServerWorld) world, 4, "", new StringTextComponent(""), world.getWorld().getServer(), null).withFeedbackDisabled(), ${input$command});
}