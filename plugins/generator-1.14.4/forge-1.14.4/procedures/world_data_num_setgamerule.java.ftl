<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
if(!world.getWorld().isRemote&&world.getWorld().getServer()!=null){
	world.getWorld().getServer().getCommandManager().handleCommand(
			new CommandSource(ICommandSource.field_213139_a_,Vec3d.ZERO,Vec2f.ZERO,(ServerWorld)world.getWorld(),
					4,"",new StringTextComponent(""),world.getWorld().getServer(),null).withFeedbackDisabled(),
			String.format("gamerule %s %d",(${generator.map(field$gamerulesnumber, "gamerules")}).toString(), ${input$gameruleValue})
	);
}
</#if>