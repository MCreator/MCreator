<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new WaterAvoidingRandomStrollGoal(this, ${field$speed})<@conditionCode field$condition/>);