<#include "procedures.java.ftl">
@EventBusSubscriber public class ${name}Procedure {
	static boolean alreadyExecuted = false;
	@SubscribeEvent public static void onWorldTick(LevelTickEvent.Post event) {
		Level world = event.getLevel();
    	if (!world.isClientSide() && world.dimension() == Level.OVERWORLD) {
        	long time = world.getDayTime() % 24000;
            if (time == 13000) {
            	if (!alreadyExecuted) {
            		alreadyExecuted = true;
            		<#assign dependenciesCode><#compress>
            		<@procedureDependenciesCode dependencies, {
                		"world": "event.getLevel()",
                		"event": "event"
                	}/>
                	</#compress></#assign>
                	execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
                }
        	} else { alreadyExecuted = false; }
        }
	}