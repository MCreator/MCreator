DamagingProjectileEntity entityToSpawn = new ${generator.map(field$fireballprojectile, "projectiles", 0)}(${generator.map(field$fireballprojectile, "projectiles", 1)}, spawnWorld);
entityToSpawn.shootingEntity = (${input$shooter} instanceof LivingEntity ? (LivingEntity) ${input$shooter} : null);
entityToSpawn.accelerationX = ${input$ax};
entityToSpawn.accelerationY = ${input$ay};
entityToSpawn.accelerationZ = ${input$az};
if (entityToSpawn instanceof FireballEntity)
    ((FireballEntity) entityToSpawn).explosionPower = (int) ${input$power};