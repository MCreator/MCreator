new Object() {
    public Entity getProjectile(World world, Entity shooter) {
        ThrowableEntity entityToSpawn;
        if (shooter instanceof LivingEntity)
            entityToSpawn = new ${generator.map(field$throwableprojectile, "projectiles", 0)}(world, (LivingEntity) shooter);
        else
        entityToSpawn = new ${generator.map(field$throwableprojectile, "projectiles", 0)}(${generator.map(field$throwableprojectile, "projectiles", 1)}, world);
        return entityToSpawn;
}}.getProjectile(world.getWorld(), ${input$shooter})