world.afterEvents.worldLoad.subscribe(() => {
    <#list localvariables as var><@var.getType().getScopeDefinition(generator.getWorkspace(), "LOCAL")['init']?interpret/></#list>

	${scriptcode}
});