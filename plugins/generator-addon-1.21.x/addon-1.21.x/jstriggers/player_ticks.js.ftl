import { world, system } from "@minecraft/server";

system.runInterval(() => {
    for (const entity of world.getPlayers()) {
        var dimension = entity.dimension;
        var x = entity.location.x;
        var y = entity.location.y;
        var z = entity.location.z;
		${scriptcode}
    }
}, 1);

${extra_templates_code}