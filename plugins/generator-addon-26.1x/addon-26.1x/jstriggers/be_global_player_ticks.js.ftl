<#include "scripts.java.ftl">

system.runInterval(() => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    for (const entity of world.getPlayers()) {
        <@optionalDependencies dependencies, {
            "x": "entity.location.x",
            "y": "entity.location.y",
            "z": "entity.location.z",
            "dimension": "entity.dimension"
        }/>
		${scriptcode}
    }
}, 1);