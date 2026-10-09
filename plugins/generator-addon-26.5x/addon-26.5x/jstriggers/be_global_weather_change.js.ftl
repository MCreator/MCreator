<#include "scripts.java.ftl">

world.afterEvents.weatherChange.subscribe((event) => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

    <@optionalDependencies dependencies, {
        "dimension": "event.dimension"
    }/>
	${scriptcode}
});