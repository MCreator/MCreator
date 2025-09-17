<#include "mcelements.ftl">
<@head>if (world instanceof ServerLevel _level) {</@head>
	LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
	entityToSpawn.moveTo(Vec3.atBottomCenterOf(${toBlockPos(input$x,input$y,input$z)}));
	<#if (field$effectOnly!"FALSE") == "TRUE">entityToSpawn.setVisualOnly(true)</#if>;
	_level.addFreshEntity(entityToSpawn);
<@tail>}</@tail>