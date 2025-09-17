<#include "mcelements.ftl">
<@head>if (world instanceof ServerLevel _level) {</@head>
	_level.holderOrThrow(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.parse("${generator.map(field$feature, "configuredfeatures")}")))
	.value().place(_level, _level.getChunkSource().getGenerator(), _level.getRandom(), ${toBlockPos(input$x,input$y,input$z)});
<@tail>}</@tail>