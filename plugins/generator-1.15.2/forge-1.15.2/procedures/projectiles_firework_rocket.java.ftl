<#include "mcitems.ftl">
new Object() {
    public Entity getFireworkRocket(World world, ItemStack stack, Entity boostedEntity) {
        FireworkRocketEntity entityToSpawn;
        if (boostedEntity instanceof LivingEntity)
            entityToSpawn = new FireworkRocketEntity(world, stack, (LivingEntity) boostedEntity);
        else
            entityToSpawn = new FireworkRocketEntity(world, 0, 0, 0, stack);
        return entityToSpawn;
}}.getFireworkRocket(world.getWorld(), ${mappedMCItemToItemStackCode(input$item, 1)}, ${input$boosted_entity})