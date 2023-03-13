<#if generator.map(field$fluids, "fluids") != "null" && generator.map(field$fluidtag, "tags") != "null">
<#if field$fluids.startsWith("CUSTOM:")>
<#assign fluid = field$fluids?replace("CUSTOM:", "")>
${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}.get().is(FluidTags.create(new ResourceLocation("${generator.map(field$fluidtag, "tags")}")))
<#else>
Fluids.${generator.map(field$fluids, "fluids")}.is(FluidTags.create(new ResourceLocation("${generator.map(field$fluidtag, "tags")}")))
</#if>
<#else>(false)</#if>