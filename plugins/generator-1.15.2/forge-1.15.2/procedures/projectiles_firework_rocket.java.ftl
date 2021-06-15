<#include "mcitems.ftl">
FireworkRocketEntity entityToSpawn;
if (${input$boosted_entity} instanceof LivingEntity)
    entityToSpawn = new FireworkRocketEntity(spawnWorld, ${mappedMCItemToItemStackCode(input$item, 1)}, (LivingEntity) ${input$boosted_entity});
else
    entityToSpawn = new FireworkRocketEntity(spawnWorld, 0, 0, 0, ${mappedMCItemToItemStackCode(input$item, 1)});