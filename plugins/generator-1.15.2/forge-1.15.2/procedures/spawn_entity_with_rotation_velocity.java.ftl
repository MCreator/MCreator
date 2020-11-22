<#assign entity = generator.map(field$entity, "entities", 1)!"null">
<#if entity != "null">
	if(world instanceof World && !world.getWorld().isRemote) {
    <#if !entity.toString().contains(".CustomEntity")>
			Entity entityToSpawn = new ${generator.map(field$entity, "entities", 0)}(EntityType.${entity}, world.getWorld());
    <#else>
			Entity entityToSpawn = new ${entity}(${entity.toString().replace(".CustomEntity", "")}.entity, world.getWorld());
    </#if>
		entityToSpawn.setLocationAndAngles(${input$x}, ${input$y}, ${input$z}, (float) ${input$yaw}, (float) ${input$pitch});
		entityToSpawn.setRenderYawOffset((float) ${input$yaw});
		entityToSpawn.setMotion(${input$vx},${input$vy},${input$vz});

		if (entityToSpawn instanceof MobEntity)
			((MobEntity)entityToSpawn).onInitialSpawn(world,world.getDifficultyForLocation(new BlockPos(entityToSpawn)), SpawnReason.MOB_SUMMONED, (ILivingEntityData) null, (CompoundNBT) null);

		world.addEntity(entityToSpawn);
	}
</#if>