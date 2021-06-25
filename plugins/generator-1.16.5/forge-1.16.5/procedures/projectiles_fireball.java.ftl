new Object() {
    public ProjectileEntity getFireball(World world, Entity shooter, double ax, double ay, double az, int power) {
        DamagingProjectileEntity entityToSpawn = new ${generator.map(field$fireballprojectile, "projectiles", 0)}(${generator.map(field$fireballprojectile, "projectiles", 1)}, world);
        entityToSpawn.setShooter(shooter);
        entityToSpawn.accelerationX = ax;
        entityToSpawn.accelerationY = ay;
        entityToSpawn.accelerationZ = az;
        if (entityToSpawn instanceof FireballEntity)
            ((FireballEntity) entityToSpawn).explosionPower = power;
        return entityToSpawn;
}}.getFireball((World) world, ${input$shooter}, ${input$ax}, ${input$ay}, ${input$az}, (int) ${input$power})