<#include "mcitems.ftl">
(${mappedBlockToBlockStateCode(input$a)}.getMaterial() == net.minecraft.block.material.Material.${generator.map(field$material, "materials")})