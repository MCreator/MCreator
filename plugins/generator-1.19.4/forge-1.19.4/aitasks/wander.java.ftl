<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new RandomStrollGoal(this, ${field$speed})<@conditionCode field$condition/>);