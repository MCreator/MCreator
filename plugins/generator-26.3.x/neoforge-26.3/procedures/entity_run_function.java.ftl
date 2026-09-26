<#include "mcelements.ftl">
{
	Entity _ent = ${input$entity};
	if (_ent.level() instanceof ServerLevel _serverLevel && _serverLevel.getServer() != null) {
		Optional<CommandFunction<CommandSourceStack>> _fopt = _serverLevel.getServer().getFunctions().get(${toIdentifier(input$function)});
		if (_fopt.isPresent())
			_serverLevel.getServer().getFunctions().execute(_fopt.get(), _ent.createCommandSourceStackForNameResolution(_serverLevel));
	}
}