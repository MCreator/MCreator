<#include "mcelements.ftl">
(world instanceof ServerLevel _level${cbi} && _level${cbi}.holderOrThrow(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.parse("${generator.map(field$feature, "configuredfeatures")}")))
	.value().place(_level${cbi}, _level${cbi}.getChunkSource().getGenerator(), _level${cbi}.getRandom(), ${toBlockPos(input$x,input$y,input$z)}))