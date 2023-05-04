<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new RandomLookAroundGoal(this)<@conditionCode field$condition/>);