<#include "scripts.java.ftl">

const ${name} = {
    onHitEntity(event) {
        <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    	<@optionalDependencies dependencies, {
			"itemstack": "event.itemStack",
			"dimension": "event.hitEntity.dimension",
			"x": "event.hitEntity.location.x",
			"y": "event.hitEntity.location.y",
			"z": "event.hitEntity.location.z",
			"entity": "event.hitEntity",
			"sourceentity": "event.attackingEntity"
		}/>
		${scriptcode}
    }
};

system.beforeEvents.startup.subscribe(({ itemComponentRegistry }) => {
    itemComponentRegistry.registerCustomComponent("${modid}:${registryname}", ${name});
});