<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new PanicGoal(this, ${field$speed})<@conditionCode field$condition/>);