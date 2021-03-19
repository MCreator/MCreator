<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
	if(world instanceof ServerWorld) {
    	<#if !entity.toString().contains(".CustomEntity")>
			Entity entityToSpawn = new ${generator.map(field$entity, "entities", 0)}(EntityType.${entity}, (World) world);
    	<#else>
			Entity entityToSpawn = new ${entity}(${entity.toString().replace(".CustomEntity", "")}.entity, (World) world);
    	</#if>
		entityToSpawn.setLocationAndAngles(${input$x}, ${input$y}, ${input$z}, (float) ${input$yaw}, (float) ${input$pitch});
		entityToSpawn.setRenderYawOffset((float) ${input$yaw});

		if (entityToSpawn instanceof MobEntity)
			((MobEntity)entityToSpawn).onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(entityToSpawn.getPosition()), SpawnReason.MOB_SUMMONED, (ILivingEntityData) null, (CompoundNBT) null);

		world.addEntity(entityToSpawn);
	}
</#if>