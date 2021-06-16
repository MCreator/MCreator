ThrowableEntity entityToSpawn;
if (${input$shooter} instanceof LivingEntity)
    entityToSpawn = new ${generator.map(field$throwableprojectile, "projectiles", 0)}(spawnWorld, (LivingEntity) ${input$shooter});
else
    entityToSpawn = new ${generator.map(field$throwableprojectile, "projectiles", 0)}(${generator.map(field$throwableprojectile, "projectiles", 1)}, spawnWorld);