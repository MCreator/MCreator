<#if input$shooter == "null">
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
<@addTemplate file="utils/projectiles/projectile.java.ftl"/>
initProjectileProperties(new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel), ${input$shooter}, Vec3.ZERO)
</#if>