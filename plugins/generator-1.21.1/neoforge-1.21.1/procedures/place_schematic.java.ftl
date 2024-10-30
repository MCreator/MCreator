<#include "mcelements.ftl">
if (world instanceof ServerLevel _serverworld) {
	StructureTemplate template = _serverworld.getStructureManager().getOrCreate(ResourceLocation.fromNamespaceAndPath("${modid}", "${field$schematic}"));
	if (template != null) {
		template.placeInWorld(_serverworld,
			${toBlockPos(input$x,input$y,input$z)},
			${toBlockPos(input$x,input$y,input$z)},
			new StructurePlaceSettings()
				.setRotation(Rotation.<#if (field$rotation!'NONE') != "RANDOM">${field$rotation!'NONE'}<#else>getRandom(_serverworld.random)</#if>)
				.setMirror(Mirror.<#if (field$mirror!'NONE') != "RANDOM">${field$mirror!'NONE'}<#else>values()[_serverworld.random.nextInt(2)]</#if>)
				.setIgnoreEntities(false), _serverworld.random, 3);
	}
}