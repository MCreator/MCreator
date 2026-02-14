import { world, system } from "@minecraft/server";

system.runInterval(() => {
	${scriptcode}
}, 1);

${extra_templates_code}