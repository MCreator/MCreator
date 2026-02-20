<#include "scripts.java.ftl">

world.afterEvents.weatherChange.subscribe((event) => {
    <@optionalDependencies dependencies, {
        "dimension": "event.dimension"
    }/>
	${scriptcode}
});