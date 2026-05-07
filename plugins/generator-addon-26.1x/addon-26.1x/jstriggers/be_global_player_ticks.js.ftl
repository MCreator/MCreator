<#include "scripts.java.ftl">

system.runInterval(() => {
    for (const entity of world.getPlayers()) {
        <@optionalDependencies dependencies, {
            "x": "entity.location.x",
            "y": "entity.location.y",
            "z": "entity.location.z",
            "dimension": "entity.dimension"
        }/>
		${scriptcode}
    }
}, 1);