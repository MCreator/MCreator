<#include "mcelements.ftl">
if (world instanceof ServerLevel _level) {
	LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level, EntitySpawnReason.TRIGGERED);
	entityToSpawn.snapTo(Vec3.atBottomCenterOf(${toBlockPos(input$x,input$y,input$z)}));
	<#if (field$effectOnly!"FALSE") == "TRUE">entityToSpawn.setVisualOnly(true)</#if>;
	_level.addFreshEntity(entityToSpawn);
}