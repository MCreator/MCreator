<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new WaterAvoidingRandomWalkingGoal(this, ${field$speed})<@conditionCode field$condition/>);