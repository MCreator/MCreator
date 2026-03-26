<#include "mcelements.ftl">
if (world instanceof ServerLevel _level && _level.getServer() != null) {
	Optional<CommandFunction<CommandSourceStack>> _fopt = _level.getServer().getFunctions().get(${toResourceLocation(input$function)});
	if(_fopt.isPresent())
		_level.getServer().getFunctions().execute(_fopt.get(),
			new CommandSourceStack(CommandSource.NULL, new Vec3(${input$x}, ${input$y}, ${input$z}), Vec2.ZERO,
				_level, 4, "", Component.literal(""), _level.getServer(), null));
}