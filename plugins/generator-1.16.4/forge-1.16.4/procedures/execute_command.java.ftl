if (world instanceof ServerWorld) {
    ((World) world).getServer().getCommandManager().handleCommand(
    new CommandSource(ICommandSource.DUMMY, new Vector3d(${input$x}, ${input$y}, ${input$z}), Vector2f.ZERO,
    (ServerWorld) world, 4, "", new StringTextComponent(""), ((World) world).getServer(), null).withFeedbackDisabled(), ${input$command});
}