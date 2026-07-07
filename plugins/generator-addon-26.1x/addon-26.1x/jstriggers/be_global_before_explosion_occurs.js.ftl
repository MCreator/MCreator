<#include "scripts.java.ftl">

world.beforeEvents.explosion.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "entity": "event.source",
        "dimension": "event.dimension",
        "x": "event.source.location.x",
        "y": "event.source.location.y",
        "z": "event.source.location.z"
    }/>
	${scriptcode}
});