if (!world.isRemote()) {
	MinecraftServer server = Objects.requireNonNull(world.getWorld().getServer());
	server.getCommandManager().handleCommand(
			new CommandSource(
					server,
					new Vec3d(0, 0, 0),
					new Vec2f(0, 0),
					(ServerWorld) world.getWorld(),
					4,
					"server",
					new StringTextComponent("server"),
					server,
					null
			),
			String.format("gamerule %s %d", (${generator.map(field$gamerulesnumber, "gamerulesnumber")}).getName(), ${input$gameruleValue})
	);
}