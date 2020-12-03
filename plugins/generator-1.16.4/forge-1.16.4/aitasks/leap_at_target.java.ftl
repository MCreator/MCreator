<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new LeapAtTargetGoal(this, (float)${field$speed})<@conditionCode field$condition/>);