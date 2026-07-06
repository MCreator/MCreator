<#include "scripts.java.ftl">

const ${name} = {
    onRandomTick(event) {
        <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    	<@optionalDependencies dependencies, {
			"block": "event.block.permutation",
			"dimension": "event.dimension",
			"x": "event.block.location.x",
			"y": "event.block.location.y",
			"z": "event.block.location.z"
		}/>
		${scriptcode}
    }
};

system.beforeEvents.startup.subscribe(({ blockComponentRegistry }) => {
    blockComponentRegistry.registerCustomComponent("${modid}:${registryname}", ${name});
});