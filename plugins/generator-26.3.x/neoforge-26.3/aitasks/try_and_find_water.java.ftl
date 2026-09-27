<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new TryFindLiquidGoal(this, FluidTags.WATER)<@conditionCode field$condition/>);