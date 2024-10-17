<#include "mcitems.ftl">
(${mappedBlockToBlockStateCode(input$a)}.getMaterial() == net.minecraft.world.level.material.Material.${generator.map(field$material, "materials")})