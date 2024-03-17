<#include "mcelements.ftl">
if (world instanceof ServerLevel _serverworld) {
	StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("${modid}", "${field$schematic}"));
	if (template != null) {
		template.placeInWorld(_serverworld,
			${toBlockPos(input$x,input$y,input$z)},
			${toBlockPos(input$x,input$y,input$z)},
			new StructurePlaceSettings()
				.setRotation(Rotation.${field$rotation!'NONE'})
				.setMirror(Mirror.${field$mirror!'NONE'})
				.setIgnoreEntities(false), _serverworld.random, 3);
	}
}