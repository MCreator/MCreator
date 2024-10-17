<#include "mcelements.ftl">
(world instanceof ServerLevel _level${cbi} && _level${cbi}.holderOrThrow(FeatureUtils.createKey("${generator.map(field$feature, "configuredfeatures")}"))
	.value().place(_level${cbi}, _level${cbi}.getChunkSource().getGenerator(), _level${cbi}.getRandom(), ${toBlockPos(input$x,input$y,input$z)}))