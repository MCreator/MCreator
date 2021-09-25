<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
if(world instanceof ServerLevel _level) {
	<#if !entity.toString().contains(".CustomEntity")>
	Entity entityToSpawn = new ${generator.map(field$entity, "entities", 0)}(${entity}, _level);
	<#else>
	Entity entityToSpawn = new ${entity}(${entity.toString().replace(".CustomEntity", "")}.entity, _level);
	</#if>
	entityToSpawn.moveTo(${input$x}, ${input$y}, ${input$z}, world.getRandom().nextFloat() * 360F, 0);

	if (entityToSpawn instanceof Mob _mobToSpawn)
		_mobToSpawn.finalizeSpawn(_level, world.getCurrentDifficultyAt(entityToSpawn.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

	world.addFreshEntity(entityToSpawn);
}
</#if>