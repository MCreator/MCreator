<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
	if(world instanceof ServerWorld) {
		Entity entityToSpawn = new ${generator.map(field$entity, "entities", 0)}(${entity}, (World) world);
		entityToSpawn.setLocationAndAngles(${input$x}, ${input$y}, ${input$z}, (float) ${input$yaw}, (float) ${input$pitch});
		entityToSpawn.setRenderYawOffset((float) ${input$yaw});
		entityToSpawn.setRotationYawHead((float) ${input$yaw});
		entityToSpawn.setMotion(${input$vx},${input$vy},${input$vz});

		if (entityToSpawn instanceof MobEntity)
			((MobEntity)entityToSpawn).onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(entityToSpawn.getPosition()), SpawnReason.MOB_SUMMONED, (ILivingEntityData) null, (CompoundNBT) null);

		world.addEntity(entityToSpawn);
	}
</#if>