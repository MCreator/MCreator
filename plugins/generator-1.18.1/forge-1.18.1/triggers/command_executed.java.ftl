<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onCommand(CommandEvent event) {
		Entity entity = event.getParseResults().getContext().getSource().getEntity();
		if (entity != null) {
			<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
		    	"x": "entity.getX()",
		    	"y": "entity.getY()",
		    	"z": "entity.getZ()",
				"world": "entity.level",
				"entity": "entity",
				"command": "event.getParseResults().getReader().getString()",
				"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
		}
	}