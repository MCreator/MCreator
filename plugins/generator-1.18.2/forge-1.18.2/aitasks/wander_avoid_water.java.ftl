<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new WaterAvoidingRandomStrollGoal(this, ${field$speed})<@conditionCode field$condition/>);