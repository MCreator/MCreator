<#include "mcelements.ftl">
<#if generator.map(field$fluids, "fluids") != "null">
<#if field$fluids.startsWith("CUSTOM:")>
<#assign fluid = field$fluids?replace("CUSTOM:", "")>
${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}.get().is(FluidTags.create(${toResourceLocation(input$tag)}))
<#else>
Fluids.${generator.map(field$fluids, "fluids")}.is(FluidTags.create(${toResourceLocation(input$tag)}))
</#if>
<#else>(false)</#if>