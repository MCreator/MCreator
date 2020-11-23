<#include "mcitems.ftl">
(${mappedBlockToBlockStateCode(input$a)}.getMaterial() == Material.${generator.map(field$material, "materials")})