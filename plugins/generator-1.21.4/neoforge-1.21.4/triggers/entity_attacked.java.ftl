<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	@SubscribeEvent public static void onEntityAttacked(LivingIncomingDamageEvent event) {
		if (event.getEntity() != null) {
			AtomicDouble amountAccessor = new AtomicDouble(event.getAmount());
			<#assign dependenciesCode><#compress>
				<@procedureDependenciesCode dependencies, {
					"x": "event.getEntity().getX()",
					"y": "event.getEntity().getY()",
					"z": "event.getEntity().getZ()",
					"amount": "event.getAmount()",
					"amountaccessor": "amountAccessor",
					"world": "event.getEntity().level()",
					"entity": "event.getEntity()",
					"damagesource": "event.getSource()",
					"sourceentity": "event.getSource().getEntity()",
					"immediatesourceentity": "event.getSource().getDirectEntity()",
					"event": "event"
				}/>
			</#compress></#assign>
			execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
			event.setAmount(amountAccessor.floatValue());
		}
	}