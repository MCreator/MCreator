<#include "scripts.java.ftl">

const ${name} = {
    onBreak(event) {
    	<@optionalDependencies dependencies, {
			"block": "event.brokenBlockPermutation",
			"dimension": "event.dimension",
			"x": "event.block.location.x",
			"y": "event.block.location.y",
			"z": "event.block.location.z",
			"entity": "event.entitySource"
		}/>
		${scriptcode}
    }
};

system.beforeEvents.startup.subscribe(({ blockComponentRegistry }) => {
    blockComponentRegistry.registerCustomComponent("${modid}:${registryname}", ${name});
});