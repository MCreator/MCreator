<#include "mcelements.ftl">
if (world instanceof ServerLevel _level)
	_level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
	.getHolderOrThrow(FeatureUtils.createKey("${generator.map(field$feature, "configuredfeatures")}"))
	.value().place(_level, _level.getChunkSource().getGenerator(), _level.getRandom(), ${toBlockPos(input$x,input$y,input$z)});