new Object() {
    public ProjectileEntity getProjectile(World world, Entity shooter) {
        ProjectileEntity entityToSpawn = new ${generator.map(field$throwableprojectile, "projectiles", 0)}(${generator.map(field$throwableprojectile, "projectiles", 1)}, world);
        entityToSpawn.setShooter(shooter);
        return entityToSpawn;
}}.getProjectile((World) world, ${input$shooter})