<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new MoveTowardsVillageGoal(this, 0.5)<@conditionCode field$condition/>);