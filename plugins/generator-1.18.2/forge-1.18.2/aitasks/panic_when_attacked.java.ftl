<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1},new PanicGoal(this, ${field$speed})<@conditionCode field$condition/>);