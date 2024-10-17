<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new RandomWalkingGoal(this, ${field$speed})<@conditionCode field$condition/>);