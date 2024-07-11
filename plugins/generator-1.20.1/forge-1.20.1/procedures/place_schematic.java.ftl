<#include "mcelements.ftl">
if (world instanceof ServerLevel _serverworld) {
	StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("${modid}", "${field$schematic}"));
	if (template != null) {
		template.placeInWorld(_serverworld,
			${toBlockPos(input$x,input$y,input$z)},
			${toBlockPos(input$x,input$y,input$z)},
			new StructurePlaceSettings()
				.setRotation(Rotation.<#if field$rotation?has_content && (field$rotation!'NONE')?string != "RANDOM">${field$rotation!'NONE'}<#else>values()[_serverworld.random.nextInt(3)]</#if>)
				.setMirror(Mirror.<#if field$mirror?has_content &&  (field$mirror!'NONE')?string != "RANDOM">${field$mirror!'NONE'}<#else>values()[_serverworld.random.nextInt(2)]</#if>)
				.setIgnoreEntities(false), _serverworld.random, 3);
	}
}
