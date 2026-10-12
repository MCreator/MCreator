<#include "scripts.java.ftl">

world.afterEvents.entityHurt.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "damagesource": "event.damageSource",
        "damage": "event.damage",
        "entity": "event.hurtEntity",
        "sourceentity": "event.damagesource.damagingEntity",
        "dimension": "event.hurtEntity.dimension",
        "x": "event.hurtEntity.location.x",
        "y": "event.hurtEntity.location.y",
        "z": "event.hurtEntity.location.z"
    }/>
	${scriptcode}
});