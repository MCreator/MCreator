<#include "scripts.java.ftl">

world.afterEvents.entityHitEntity.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "sourceentity": "event.damagingEntity",
        "entity": "event.hitEntity",
        "dimension": "event.damagingEntity.dimension",
        "x": "event.damagingEntity.location.x",
        "y": "event.damagingEntity.location.y",
        "z": "event.damagingEntity.location.z"
    }/>
	${scriptcode}
});