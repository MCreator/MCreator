<#include "mcelements.ftl">
if (world instanceof ServerLevel _level && _level.getServer() != null)
	_level.getServer().getFunctions().get(${toResourceLocation(input$function)}).ifPresent(_cf -> {
		_level.getServer().getFunctions().execute(_cf,
			new CommandSourceStack(CommandSource.NULL, new Vec3(${input$x}, ${input$y}, ${input$z}), Vec2.ZERO,
				_level, 4, "", new TextComponent(""), _level.getServer(), null));
	});