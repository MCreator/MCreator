<#include "scripts.java.ftl">

world.afterEvents.entityDie.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "damagesource": "event.damageSource",
        "sourceentity": "event.damageSource.damagingEntity",
        "entity": "event.deadEntity",
        "dimension": "event.deadEntity.dimension",
        "x": "event.deadEntity.location.x",
        "y": "event.deadEntity.location.y",
        "z": "event.deadEntity.location.z"
    }/>
	${scriptcode}
});