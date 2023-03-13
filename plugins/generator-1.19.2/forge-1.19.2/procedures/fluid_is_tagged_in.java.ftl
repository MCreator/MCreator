<#if generator.map(field$fluids, "fluids") != "null" && generator.map(field$fluidtag, "tags") != "null">
Fluids.${generator.map(field$fluids, "fluids")}.is(FluidTags.create(new ResourceLocation("${generator.map(field$fluidtag, "tags")}")))
<#else>(false)</#if>