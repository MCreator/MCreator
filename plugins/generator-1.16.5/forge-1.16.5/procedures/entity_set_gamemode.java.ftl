if(${input$entity} instanceof PlayerEntity)
	((PlayerEntity)${input$entity}).setGameType(GameType.${generator.map(field$gamemode, "gamemodes")});