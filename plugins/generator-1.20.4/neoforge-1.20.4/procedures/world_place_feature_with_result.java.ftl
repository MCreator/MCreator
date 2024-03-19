<#include "mcelements.ftl">
(world instanceof ServerLevel _level${cbi} && _level${cbi}.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
	.getHolderOrThrow(ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation("${generator.map(field$feature, "configuredfeatures")}")))
	.value().place(_level${cbi}, _level${cbi}.getChunkSource().getGenerator(), _level${cbi}.getRandom(), ${toBlockPos(input$x,input$y,input$z)}))