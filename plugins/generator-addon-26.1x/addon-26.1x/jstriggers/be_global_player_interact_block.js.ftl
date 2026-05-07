<#include "scripts.java.ftl">

world.beforeEvents.playerInteractWithBlock.subscribe((event) => {
    <@optionalDependencies dependencies, {
        "block": "event.block.permutation",
        "dimension": "event.player.dimension",
        "x": "event.block.location.x",
        "y": "event.block.location.y",
        "z": "event.block.location.z",
        "entity": "event.player"
    }/>
    system.run(() => {
        ${scriptcode}
    });
});