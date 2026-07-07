<#include "scripts.java.ftl">

world.afterEvents.itemCompleteUse.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "entity": "event.source",
		"x": "event.source.location.x",
		"y": "event.source.location.y",
		"z": "event.source.location.z",
		"dimension": "event.source.dimension",
		"useDuration": "event.useDuration",
		"itemstack": "event.itemStack"
    }/>
	${scriptcode}
});