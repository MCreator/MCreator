<#include "scripts.java.ftl">

const ${name} = {
    onConsume(event) {
    	<@optionalDependencies dependencies, {
			"itemstack": "event.itemStack",
			"dimension": "event.source.dimension",
			"x": "event.source.location.x",
			"y": "event.source.location.y",
			"z": "event.source.location.z",
			"entity": "event.source"
		}/>
		${scriptcode}
    }
};

system.beforeEvents.startup.subscribe(({ itemComponentRegistry }) => {
    itemComponentRegistry.registerCustomComponent("${modid}:${registryname}", ${name});
});