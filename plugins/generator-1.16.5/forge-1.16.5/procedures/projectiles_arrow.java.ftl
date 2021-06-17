<#assign projectile = generator.map(field$rangeditem, "projectiles", 1)?replace(".ArrowCustomEntity", ".arrow")>
AbstractArrowEntity entityToSpawn = new ${generator.map(field$rangeditem, "projectiles", 0)}(${projectile}, spawnWorld);
entityToSpawn.setShooter(${input$shooter});
entityToSpawn.setDamage((float) ${input$damage});
entityToSpawn.setKnockbackStrength((int) ${input$knockback});
entityToSpawn.setPierceLevel((byte) ${input$piercing});
<#if field$fire?lower_case == "true">entityToSpawn.setFire(100);</#if>
<#if field$particles?lower_case == "true">entityToSpawn.setIsCritical(true);</#if>