<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
if(world instanceof ServerLevel _level) {
	<#if !field$entity?starts_with("CUSTOM:")>
	Entity entityToSpawn = new ${generator.map(field$entity, "entities", 0)}(${entity}, _level);
   	<#else>
	Entity entityToSpawn = new ${entity}(${JavaModName}Entities.${generator.getRegistryNameForModElement(field$entity?replace("CUSTOM:", ""))?upper_case}, _level);
   	</#if>
	entityToSpawn.moveTo(${input$x}, ${input$y}, ${input$z}, ${opt.toFloat(input$yaw)}, ${opt.toFloat(input$pitch)});
	entityToSpawn.setYBodyRot(${opt.toFloat(input$yaw)});
	entityToSpawn.setYHeadRot(${opt.toFloat(input$yaw)});
	entityToSpawn.setDeltaMovement(${input$vx},${input$vy},${input$vz});

	if (entityToSpawn instanceof Mob _mobToSpawn)
		_mobToSpawn.finalizeSpawn(_level, world.getCurrentDifficultyAt(entityToSpawn.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

	world.addFreshEntity(entityToSpawn);
}
</#if>