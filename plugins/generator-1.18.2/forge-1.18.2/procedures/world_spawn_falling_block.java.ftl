<#include "mcitems.ftl">
if (world instanceof ServerLevel _level) {
	FallingBlockEntity.fall(_level, new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}), ${mappedBlockToBlockStateCode(input$block)});
}