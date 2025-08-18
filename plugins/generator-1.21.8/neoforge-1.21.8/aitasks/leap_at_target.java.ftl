<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new LeapAtTargetGoal(this, (float) ${field$speed})<@conditionCode field$condition/>);