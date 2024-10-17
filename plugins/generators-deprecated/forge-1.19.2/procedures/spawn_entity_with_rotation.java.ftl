<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
if (world instanceof ServerLevel _level) {
	Entity entityToSpawn = new ${generator.map(field$entity, "entities", 0)}(${entity}, _level);
	entityToSpawn.moveTo(${input$x}, ${input$y}, ${input$z}, ${opt.toFloat(input$yaw)}, ${opt.toFloat(input$pitch)});
	entityToSpawn.setYBodyRot(${opt.toFloat(input$yaw)});
	entityToSpawn.setYHeadRot(${opt.toFloat(input$yaw)});

	if (entityToSpawn instanceof Mob _mobToSpawn)
		_mobToSpawn.finalizeSpawn(_level, world.getCurrentDifficultyAt(entityToSpawn.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

	world.addFreshEntity(entityToSpawn);
}
</#if>