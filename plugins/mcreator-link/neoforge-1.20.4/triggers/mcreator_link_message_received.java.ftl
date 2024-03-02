<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onMCreatorLinkMessageReceived(LinkCustomMessageReceivedEvent event){
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"message": "new String(event.getData())",
				"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}