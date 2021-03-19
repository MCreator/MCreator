<#if generator.map(field$gamerulesnumber, "gamerules") != "null">
	if(world instanceof ServerWorld && ((World) world).getServer() != null) {
		((World) world).getServer().getCommandManager().handleCommand(
				new CommandSource(ICommandSource.DUMMY,Vector3d.ZERO,Vector2f.ZERO,((ServerWorld)world).getWorld(),
						4,"",new StringTextComponent(""),((World) world).getServer(),null).withFeedbackDisabled(),
				String.format("gamerule %s %d",(${generator.map(field$gamerulesnumber, "gamerules")}).toString(), ${input$gameruleValue})
		);
	}
</#if>