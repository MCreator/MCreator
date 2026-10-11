<#include "mcelements.ftl">
if (world instanceof ServerLevel _level) {
	BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
	if (BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), _level, _bp, null) || BoneMealItem.growWaterPlant(new ItemStack(Items.BONE_MEAL), _level, _bp, null)) {
		_level.levelEvent(2005, _bp, 0);
	}
}