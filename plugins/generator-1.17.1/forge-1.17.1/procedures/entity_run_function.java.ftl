<#include "mcelements.ftl">
{
	Entity _ent = ${input$entity};
	if(!_ent.level.isClientSide() && _ent.getServer() != null) {
		Optional<CommandFunction> _fopt = _ent.getServer().getFunctions().get(${toResourceLocation(input$function)});
		if(_fopt.isPresent())
			_ent.getServer().getFunctions().execute(_fopt.get(), _ent.createCommandSourceStack());
	}
}