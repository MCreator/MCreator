<#include "scripts.java.ftl">

world.afterEvents.entityHitBlock.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "direction": "event.blockFace",
        "entity": "event.damagingEntity",
        "dimension": "event.damagingEntity.dimension",
        "x": "event.hitBlock.location.x",
        "y": "event.hitBlock.location.y",
        "z": "event.hitBlock.location.z",
        "block": "event.hitBlockPermutation"
    }/>
	${scriptcode}
});