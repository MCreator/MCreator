import { world, system } from "@minecraft/server";

system.runInterval(() => {
    for (const entity of world.getPlayers()) {
        const dimension = entity.dimension;
        const x = entity.location.x;
        const y = entity.location.y;
        const z = entity.location.z;
		${scriptcode}
    }
}, 1);

${extra_templates_code}