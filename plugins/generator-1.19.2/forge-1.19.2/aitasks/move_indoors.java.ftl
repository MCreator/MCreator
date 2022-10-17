<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new MoveBackToVillageGoal(this, 0.6, false)<@conditionCode field$condition/>);