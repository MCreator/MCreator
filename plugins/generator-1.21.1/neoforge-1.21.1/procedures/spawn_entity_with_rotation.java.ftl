<#include "mcelements.ftl">
<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
if (world instanceof ServerLevel _level) {
	Entity entityToSpawn = ${entity}.spawn(_level, ${toBlockPos(input$x,input$y,input$z)}, MobSpawnType.MOB_SUMMONED);
	if (entityToSpawn != null) {
		<#if input$yaw != "/*@int*/0">
			entityToSpawn.setYRot(${opt.toFloat(input$yaw)});
			entityToSpawn.setYBodyRot(${opt.toFloat(input$yaw)});
			entityToSpawn.setYHeadRot(${opt.toFloat(input$yaw)});
		</#if>
		<#if input$pitch != "/*@int*/0">
			entityToSpawn.setXRot(${opt.toFloat(input$pitch)});
		</#if>
	}
}
</#if>