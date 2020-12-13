<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
if(!((World) world).getWorld().isRemote && ((World) world).getWorld().getServer() != null){
	((World) world).getWorld().getServer().getCommandManager().handleCommand(
			new CommandSource(ICommandSource.DUMMY,Vector3d.ZERO,Vector2f.ZERO,((ServerWorld)world).getWorld(),
					4,"",new StringTextComponent(""),((World) world).getWorld().getServer(),null).withFeedbackDisabled(),
			String.format("gamerule %s %d",(${generator.map(field$gamerulesnumber, "gamerules")}).toString(), ${input$gameruleValue})
	);
}
</#if>