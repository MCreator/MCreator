<#include "scripts.java.ftl">

world.afterEvents.entityHeal.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "healing": "event.healing",
        "entity": "event.healedEntity",
        "dimension": "event.healedEntity.dimension",
        "x": "event.healedEntity.location.x",
        "y": "event.healedEntity.location.y",
        "z": "event.healedEntity.location.z"
    }/>
	${scriptcode}
});