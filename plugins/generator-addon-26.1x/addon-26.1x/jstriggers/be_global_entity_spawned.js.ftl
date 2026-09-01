<#include "scripts.java.ftl">

world.afterEvents.entitySpawn.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "entity": "event.entity",
        "dimension": "event.entity.dimension",
        "x": "event.entity.location.x",
        "y": "event.entity.location.y",
        "z": "event.entity.location.z"
    }/>
	${scriptcode}
});