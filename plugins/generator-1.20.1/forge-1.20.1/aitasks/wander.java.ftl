<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new RandomStrollGoal(this, ${field$speed})<@conditionCode field$condition/>);