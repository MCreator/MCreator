<#include "scripts.java.ftl">

world.afterEvents.playerPlaceBlock.subscribe((event) => {
    <@optionalDependencies dependencies, {
        "block": "event.block.permutation",
        "dimension": "event.dimension",
        "x": "event.block.location.x",
        "y": "event.block.location.y",
        "z": "event.block.location.z",
        "entity": "event.player"
    }/>
	${scriptcode}
});