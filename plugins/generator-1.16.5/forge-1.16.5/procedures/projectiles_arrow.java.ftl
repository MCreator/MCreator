<#assign projectile = generator.map(field$rangeditem, "projectiles", 1)?replace(".ArrowCustomEntity", ".arrow")>
AbstractArrowEntity entityToSpawn = new ${generator.map(field$rangeditem, "projectiles", 0)}(${projectile}, spawnWorld);
entityToSpawn.setShooter(${input$shooter});
entityToSpawn.setDamage((float) ${input$damage});
entityToSpawn.setKnockbackStrength((int) ${input$knockback});