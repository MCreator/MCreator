<#include "mcelements.ftl">
/*@int*/(TierSortingRegistry.getSortedTiers().stream().filter(t -> t.getTag() != null &&
	world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).is(t.getTag())).map(Tier::getLevel).findFirst().orElse(0))