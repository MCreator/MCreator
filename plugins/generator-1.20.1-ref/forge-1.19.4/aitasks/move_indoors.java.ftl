<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new MoveBackToVillageGoal(this, 0.6, false)<@conditionCode field$condition/>);