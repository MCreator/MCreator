<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new RandomSwimmingGoal(this, ${field$speed}, 40)<@conditionCode field$condition/>);