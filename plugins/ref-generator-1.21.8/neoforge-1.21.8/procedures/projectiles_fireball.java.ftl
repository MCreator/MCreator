<#if (input$shooter == "null") && ((input$ax == "/*@int*/0") && (input$ay == "/*@int*/0") && (input$az == "/*@int*/0"))>
new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel)
<#else>
<@addTemplate file="utils/projectiles/projectile.java.ftl"/>
initProjectileProperties(new ${generator.map(field$projectile, "projectiles", 0)}(${generator.map(field$projectile, "projectiles", 1)}, projectileLevel), ${input$shooter}, new Vec3(${input$ax}, ${input$ay}, ${input$az}))
</#if>