<#include "procedures.java.ftl">
@Mod.EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onMCreatorLinkPinChanged(LinkDigitalPinChangedEvent event){
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
				"pin": "event.getPin()",
				"value": "(int) event.getValue()",
				"risingEdge": "event.isRisingEdge()",
				"event": "event"
			}/>
		</#compress></#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}