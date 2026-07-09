<#include "scripts.java.ftl">

world.afterEvents.entityHitBlock.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "direction": "event.blockFace",
        "entity": "event.damagingEntity",
        "dimension": "event.damagingEntity.dimension",
        "x": "event.damagingEntity.location.x",
        "y": "event.damagingEntity.location.y",
        "z": "event.damagingEntity.location.z",
        "block": "event.hitBlockPermutation"
    }/>
	${scriptcode}
});