<#include "mcelements.ftl">
<#assign sound = generator.map(field$sound, "sounds")?replace("CUSTOM:", "${modid}:")>
<#if sound?has_content>
if (world instanceof Level _level) {
	if (!_level.isClientSide()) {
		_level.playSound(null, ${toBlockPos(input$x,input$y,input$z)},
			BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${sound}")),
			SoundSource.${generator.map(field$soundcategory!"neutral", "soundcategories")}, ${opt.toFloat(input$level)}, ${opt.toFloat(input$pitch)});
	} else {
		_level.playLocalSound(${input$x}, ${input$y}, ${input$z},
			BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("${sound}")),
			SoundSource.${generator.map(field$soundcategory!"neutral", "soundcategories")}, ${opt.toFloat(input$level)}, ${opt.toFloat(input$pitch)}, false);
	}
}
</#if>