<#assign projectile = generator.map(field$rangeditem, "projectiles", 1)?replace(".ArrowCustomEntity", ".arrow")>
new Object() {
    public Entity getArrow(World world, Entity shooter, float damage, int knockback, byte piercing) {
        AbstractArrowEntity entityToSpawn = new ${generator.map(field$rangeditem, "projectiles", 0)}(${projectile}, world);
        entityToSpawn.setShooter(shooter);
        entityToSpawn.setDamage(damage);
        entityToSpawn.setKnockbackStrength(knockback);
        entityToSpawn.setPierceLevel(piercing);
        <#if field$fire?lower_case == "true">entityToSpawn.setFire(100);</#if>
        <#if field$particles?lower_case == "true">entityToSpawn.setIsCritical(true);</#if>
        return entityToSpawn;
}}.getArrow(world.getWorld(), ${input$shooter}, (float) ${input$damage}, (int) ${input$knockback}, (byte) ${input$piercing})