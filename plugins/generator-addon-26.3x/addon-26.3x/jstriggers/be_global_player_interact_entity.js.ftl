<#include "scripts.java.ftl">

world.afterEvents.playerInteractWithEntity.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "entity": "event.target",
        "sourceentity": "event.player",
		"x": "event.target.location.x",
		"y": "event.target.location.y",
		"z": "event.target.location.z",
		"dimension": "event.target.dimension"
    }/>
	${scriptcode}
});