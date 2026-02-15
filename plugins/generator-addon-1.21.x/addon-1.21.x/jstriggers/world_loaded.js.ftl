import { world, system } from "@minecraft/server";

world.afterEvents.worldLoad.subscribe(() => {
	${scriptcode}
});

${extra_templates_code}