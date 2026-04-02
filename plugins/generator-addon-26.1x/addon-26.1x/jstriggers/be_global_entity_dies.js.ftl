<#include "scripts.java.ftl">

world.afterEvents.entityDie.subscribe((event) => {
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