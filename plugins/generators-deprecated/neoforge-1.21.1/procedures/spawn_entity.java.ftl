<#include "mcelements.ftl">
<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
if (world instanceof ServerLevel _level) {
	Entity entityToSpawn = ${entity}.spawn(_level, ${toBlockPos(input$x,input$y,input$z)}, MobSpawnType.MOB_SUMMONED);
	if (entityToSpawn != null) {
		entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
	}
}
</#if>