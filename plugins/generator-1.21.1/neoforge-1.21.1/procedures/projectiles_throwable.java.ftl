<#if input$shooter == "null">
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
<@addTemplate file="utils/projectiles/throwable.java.ftl"/>
initThrowableProjectile(new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel), ${input$shooter}, Vec3.ZERO)
</#if>