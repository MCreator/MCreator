<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new RandomSwimmingGoal(this, ${field$speed}, 40)<@conditionCode field$condition/>);