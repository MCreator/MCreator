<#include "mcitems.ftl">
new Object() {
    public ProjectileEntity getFireworkRocket(World world, ItemStack stack, Entity boostedEntity, Entity shooter) {
        FireworkRocketEntity entityToSpawn;
        if (boostedEntity instanceof LivingEntity)
            entityToSpawn = new FireworkRocketEntity(world, stack, (LivingEntity) boostedEntity);
        else
            entityToSpawn = new FireworkRocketEntity(world, 0, 0, 0, stack);
        entityToSpawn.setShooter(shooter);
        return entityToSpawn;
}}.getFireworkRocket((World) world, ${mappedMCItemToItemStackCode(input$item, 1)}, ${input$boosted_entity}, ${input$shooter})