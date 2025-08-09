<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new FollowBoatGoal(this)<@conditionCode field$condition/>);