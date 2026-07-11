<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new RandomLookAroundGoal(this)<@conditionCode field$condition/>);