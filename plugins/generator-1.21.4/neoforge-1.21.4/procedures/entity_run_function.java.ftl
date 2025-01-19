<#include "mcelements.ftl">
{
	Entity _ent = ${input$entity};
	if (_ent.level() instanceof ServerLevel _serverLevel && _ent.getServer() != null) {
		Optional<CommandFunction<CommandSourceStack>> _fopt = _ent.getServer().getFunctions().get(${toResourceLocation(input$function)});
		if (_fopt.isPresent())
			_ent.getServer().getFunctions().execute(_fopt.get(), _ent.createCommandSourceStackForNameResolution(_serverLevel));
	}
}