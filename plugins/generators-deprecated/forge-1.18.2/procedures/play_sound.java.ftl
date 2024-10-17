<#include "mcelements.ftl">
if (world instanceof Level _level) {
	if (!_level.isClientSide()) {
		_level.playSound(null, ${toBlockPos(input$x,input$y,input$z)},
	    	ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
			SoundSource.${generator.map(field$soundcategory!"neutral", "soundcategories")}, ${opt.toFloat(input$level)}, ${opt.toFloat(input$pitch)});
	} else {
		_level.playLocalSound(${input$x}, ${input$y}, ${input$z},
	    	ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")}")),
			SoundSource.${generator.map(field$soundcategory!"neutral", "soundcategories")}, ${opt.toFloat(input$level)}, ${opt.toFloat(input$pitch)}, false);
	}
}