<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new EatBlockGoal(this)<@conditionCode field$condition/>);