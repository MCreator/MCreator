<#include "mcelements.ftl">
<#if generator.map(field$fluids, "fluids") != "null">
Fluids.${generator.map(field$fluids, "fluids")}.is(FluidTags.create(${toResourceLocation(input$tag)}))
<#else>(false)</#if>