<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onMCreatorLinkPinChanged(LinkDigitalPinChangedEvent event){
		<#assign dependenciesCode>
			<@procedureDependenciesCode dependencies, {
				"pin": "event.getPin()",
				"value": "(int) event.getValue()",
				"risingEdge": "event.isRisingEdge()",
				"event": "event"
			}/>
		</#assign>
		execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
	}